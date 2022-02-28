/**
 * ownCloud Android client application
 *
 * @author Bartek Przybylski
 * @author Christian Schabesberger
 * Copyright (C) 2012 Bartek Przybylski
 * Copyright (C) 2020 ownCloud GmbH.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.educamadrid.cloudeducamadrid.ui.activity;

import com.educamadrid.cloudeducamadrid.datamodel.FileDataStorageManager;
import com.educamadrid.cloudeducamadrid.files.services.FileDownloader.FileDownloaderBinder;
import com.educamadrid.cloudeducamadrid.files.services.FileUploader.FileUploaderBinder;
import com.educamadrid.cloudeducamadrid.services.OperationsService.OperationsServiceBinder;
import com.educamadrid.cloudeducamadrid.ui.helpers.FileOperationsHelper;

public interface ComponentsGetter {

    /**
     * To be invoked when the parent activity is fully created to get a reference
     * to the FileDownloader service API.
     */
    FileDownloaderBinder getFileDownloaderBinder();

    /**
     * To be invoked when the parent activity is fully created to get a reference
     * to the FileUploader service API.
     */
    FileUploaderBinder getFileUploaderBinder();

    /**
     * To be invoked when the parent activity is fully created to get a reference
     * to the OperationsService service API.
     */
    OperationsServiceBinder getOperationsServiceBinder();

    FileDataStorageManager getStorageManager();

    FileOperationsHelper getFileOperationsHelper();

}
