package com.dreamteam.breakloop.domain

data class ReductionProgress (
    val baselineMs: Long,
    val changePercent: Int,
    val progressTowardTarget: Float
){
}