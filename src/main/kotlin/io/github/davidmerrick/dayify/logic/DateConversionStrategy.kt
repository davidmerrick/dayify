package io.github.davidmerrick.dayify.logic

import biweekly.property.DateEnd
import biweekly.property.DateStart

interface DateConversionStrategy {
    fun convertStartDate(date: DateStart): DateStart
    fun convertEndDate(date: DateEnd): DateEnd
}
