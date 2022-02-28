package com.educamadrid.cloudeducamadrid.data.server.datasources

import com.educamadrid.cloudeducamadrid.domain.server.model.ServerInfo

interface RemoteServerInfoDataSource {
    fun getServerInfo(path: String): ServerInfo
}
