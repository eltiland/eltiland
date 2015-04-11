package com.eltiland.model;

import com.eltiland.model.file.File;

/**
 * @author knorr
 * @version 1.0
 * @since 8/14/12
 */
public interface IWithAvatar extends Identifiable {

    public File getAvatar();
}
