package com.educamadrid.cloudeduca.testutil

import com.educamadrid.cloudeduca.domain.sharing.shares.model.OCShare
import com.educamadrid.cloudeduca.domain.sharing.shares.model.ShareType

val OC_SHARE = OCShare(
    shareType = ShareType.USER, // Private share by default
    shareWith = "",
    path = "/Photos/image.jpg",
    permissions = 1,
    sharedDate = 1542628397,
    expirationDate = 0,
    token = "AnyToken",
    sharedWithDisplayName = "",
    sharedWithAdditionalInfo = "",
    isFolder = false,
    remoteId = "1",
    name = "",
    shareLink = ""
)

val OC_PRIVATE_SHARE = OC_SHARE.copy(
    shareWith = "WhoEver",
    permissions = -1,
    sharedWithDisplayName = "anyDisplayName"
)

val OC_PUBLIC_SHARE = OC_SHARE.copy(
    shareType = ShareType.PUBLIC_LINK,
    expirationDate = 1000,
    name = "Image link",
    shareLink = "link"
)
