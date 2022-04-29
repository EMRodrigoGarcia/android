package com.educamadrid.cloudeduca.data.server.datasources

import com.educamadrid.cloudeduca.domain.server.model.ServerInfo

interface RemoteServerInfoDataSource {
    fun getServerInfo(path: String): ServerInfo
}
