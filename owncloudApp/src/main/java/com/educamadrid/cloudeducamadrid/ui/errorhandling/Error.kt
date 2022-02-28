package com.educamadrid.cloudeducamadrid.ui.errorhandling

import com.educamadrid.cloudeducamadrid.lib.common.operations.RemoteOperationResult.ResultCode

data class Error(var code: ResultCode, var exception: Exception)