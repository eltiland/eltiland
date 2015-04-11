/**
 * Created with IntelliJ IDEA.
 * User: Aleks
 * Date: 29.05.13
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
function createShare(link, desc) {
    new Ya.share({
        element:'ya_share1',
        elementStyle:{
            'type':'button',
            'border':true,
            'quickServices':['yaru', 'twitter', 'vkontakte', 'facebook', 'gplus']
        },
        link: link,
        description: desc,
        title:"Элтиленд - информационно-образовательная среда для детей, педагогов и родителей",
        image:'http://eltiland.ru/static/images/elti/header_center_small.png'
    });
}

