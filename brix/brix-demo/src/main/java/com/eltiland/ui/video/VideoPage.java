package com.eltiland.ui.video;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.PropertyManager;
import com.eltiland.bl.tags.TagEntityManager;
import com.eltiland.model.Property;
import com.eltiland.model.Video;
import com.eltiland.model.tags.Tag;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.ajaxradio.AjaxStringNameRadioPanel;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.tags.components.filter.TagFilterPanel;
import com.eltiland.ui.video.components.VideoInfoPanel;
import com.eltiland.ui.video.components.VideoPageAdminPanel;
import org.apache.velocity.util.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Page for video.
 *
 * @author Aleksey PLotnikov.
 */
public class VideoPage extends TwoColumnPage {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private PropertyManager propertyManager;
    @SpringBean
    private TagEntityManager tagEntityManager;

    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/video";

    private static final String PAGE_PARAM = "page";
    private static final String SORT_PARAM = "sort";
    private static final String DIRECTION_PARAM = "dir";
    private static final String FILTER_PARAM = "filter";
    private static final String FILTER_SHOW = "fs";

    private int currentSortIndex = 0;
    private boolean currentDirection = false;
    private int currentPage = 0;
    private String filterString = "";
    private boolean filterShown = false;
    private IModel<List<Tag>> filterTagModel = new GenericDBListModel<>(Tag.class);

    private String getPropertyByIndex(int index) {
        if (index == 0) {
            return "creationDate";
        } else if (index == 1) {
            return "name";
        } else if (index == 2) {
            return "duration";
        } else {
            return "viewCount";
        }
    }

    private IModel<List<Video>> videoModel = new LoadableDetachableModel<List<Video>>() {
        @Override
        protected List<Video> load() {
            List<Criterion> criterionList = new ArrayList<>();

            if (!(filterTagModel.getObject().isEmpty())) {
                List<Long> ids = tagEntityManager.getEntityIds(filterTagModel.getObject());

                if (ids.isEmpty()) {
                    return new ArrayList<>();
                }

                Disjunction disjunction = Restrictions.disjunction();
                for (Long id : ids) {
                    disjunction.add(Restrictions.eq("id", id));
                }
                criterionList.add(disjunction);
            }

            return genericManager.getEntityList(Video.class, new ArrayList<String>(), criterionList,
                    null, null, getPropertyByIndex(currentSortIndex), currentDirection, null, null);
        }
    };

    private WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");

    /**
     * Page constructor.
     *
     * @param parameters page parameters.
     */
    public VideoPage(PageParameters parameters) {
        super(parameters);

        if (parameters.getNamedKeys().contains(PAGE_PARAM)) {
            currentPage = parameters.get(PAGE_PARAM).toInt();
        }

        if (parameters.getNamedKeys().contains(SORT_PARAM)) {
            currentSortIndex = parameters.get(SORT_PARAM).toInt();
        }

        if (parameters.getNamedKeys().contains(DIRECTION_PARAM)) {
            currentDirection = parameters.get(DIRECTION_PARAM).toBoolean();
        }

        if (parameters.getNamedKeys().contains(FILTER_PARAM)) {
            filterString = parameters.get(FILTER_PARAM).toString();
            String[] tag_ids = StringUtils.split(filterString, "_");
            for (String id : tag_ids) {
                filterTagModel.getObject().add(genericManager.getObject(Tag.class, Long.parseLong(id)));
            }
        }

        if (parameters.getNamedKeys().contains(FILTER_SHOW)) {
            filterShown = parameters.get(FILTER_SHOW).toBoolean();
        }

        AjaxStringNameRadioPanel selector = new AjaxStringNameRadioPanel("selector", currentSortIndex) {
            @Override
            protected List<String> getList() {
                return new ArrayList<>(Arrays.asList(
                        getString("dateSort"),
                        getString("nameSort"),
                        getString("durationSort"),
                        getString("viewCountSort")));
            }

            @Override
            protected void onRadioSelect(AjaxRequestTarget target, int selectionIndex) {
                currentSortIndex = selectionIndex;
                videoModel.detach();
                setResponsePage(VideoPage.class, new PageParameters()
                        .add(SORT_PARAM, selectionIndex)
                        .add(DIRECTION_PARAM, currentDirection)
                        .add(FILTER_PARAM, filterString)
                        .add(FILTER_SHOW, filterShown));
            }
        };

        WebMarkupContainer directionButton = new WebMarkupContainer("dirButton");
        directionButton.add(new AttributeAppender("class",
                new Model<>(currentDirection ? "up" : "down"), " "));
        directionButton.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                currentDirection = !currentDirection;
                setResponsePage(VideoPage.class, new PageParameters()
                        .add(SORT_PARAM, currentSortIndex)
                        .add(DIRECTION_PARAM, currentDirection)
                        .add(FILTER_PARAM, filterString)
                        .add(FILTER_SHOW, filterShown));
            }
        });
        directionButton.add(new AttributeModifier("title", new ResourceModel("sortDirection")));
        directionButton.add(new TooltipBehavior());

        WebMarkupContainer adminContainer = new WebMarkupContainer("admin") {
            @Override
            public boolean isVisible() {
                User currentUser = EltilandSession.get().getCurrentUser();
                return currentUser != null && currentUser.isSuperUser();
            }
        };

        add(adminContainer);

        final int fullCount = genericManager.getEntityCount(Video.class, null, null);
        final int pageCount = Integer.parseInt(propertyManager.getProperty(Property.VIDEO_PAGING));

        final PageableListView list = new PageableListView<Video>("videoList", videoModel, pageCount) {
            @Override
            protected void populateItem(ListItem<Video> item) {
                item.add(new VideoInfoPanel("videoInfo", item.getModel()) {
                    @Override
                    protected void updateList(AjaxRequestTarget target) {
                        setResponsePage(VideoPage.class,
                                new PageParameters()
                                        .add(PAGE_PARAM, currentPage)
                                        .add(SORT_PARAM, currentSortIndex)
                                        .add(DIRECTION_PARAM, currentDirection)
                                        .add(FILTER_PARAM, filterString)
                                        .add(FILTER_SHOW, filterShown));
                    }
                }.setOutputMarkupId(true));
            }
        };
        list.setCurrentPage(currentPage);

        list.setOutputMarkupId(true);
        add(listContainer.setOutputMarkupId(true));
        listContainer.add(list);

        add(new AjaxPagingNavigator("navigator", list) {
            @Override
            public boolean isVisible() {
                return fullCount > pageCount;
            }

            @Override
            protected void onAjaxEvent(AjaxRequestTarget target) {
                super.onAjaxEvent(target);
                setResponsePage(VideoPage.class, new PageParameters().add(PAGE_PARAM, list.getCurrentPage()));
            }
        });

        adminContainer.add(new VideoPageAdminPanel("adminPanel") {
            @Override
            protected void onUpdateList(AjaxRequestTarget target) {
                // We need full redraw of the page - because of we need reloading of the scripts
                setResponsePage(VideoPage.class);
            }
        });

        TagFilterPanel filterPanel = new TagFilterPanel("tagFilterPanel", Video.class, filterShown) {
            @Override
            protected void onClick(AjaxRequestTarget target, IModel<List<Tag>> tagListModel) {
                String tagString = "";
                boolean isFirst = true;

                for (Tag tag : tagListModel.getObject()) {
                    if (!isFirst) {
                        tagString += "_";
                    } else {
                        isFirst = false;
                    }

                    tagString += tag.getId().toString();
                }

                setResponsePage(VideoPage.class,
                        new PageParameters()
                                .add(PAGE_PARAM, currentPage)
                                .add(SORT_PARAM, currentSortIndex)
                                .add(DIRECTION_PARAM, currentDirection)
                                .add(FILTER_PARAM, tagString)
                                .add(FILTER_SHOW, filterShown));
            }

            @Override
            protected void onChangeShown(AjaxRequestTarget target, boolean isShown) {
                filterShown = isShown;
            }
        };

        filterPanel.initTagList(filterTagModel);
        add(filterPanel);

        add(selector);
        add(directionButton);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_TAGS);
    }
}
