package com.educamadrid.cloudeduca.ui.errorhandling

import com.educamadrid.cloudeduca.lib.common.operations.RemoteOperationResult.ResultCode

data class Error(var code: ResultCode, var exception: Exception)