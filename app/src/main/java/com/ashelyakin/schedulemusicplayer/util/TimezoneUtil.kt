package com.ashelyakin.schedulemusicplayer.util

import com.ashelyakin.schedulemusicplayer.profile.Day
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.profile.TimeZone
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class TimezoneUtil {

    companion object{

        fun getNextTodayTimezone(timeZones: List<com.ashelyakin.schedulemusicplayer.profile.TimeZone>, timezone: TimeZone): Date? {
            val timezoneStartTime = LocalTime.parse(transformToIsoTime(timezone.from), DateTimeFormatter.ISO_TIME)
            val currentTime = LocalDateTime.now().toLocalTime()

            return if (currentTime.isBefore(timezoneStartTime))
                getDate(timezone.from, 0)
            else
                null
        }

        //счетчик рассмотренных дней для рекурсии
        private var observedDayCount = 1
        fun getNextDayFirstTimezone(days: List<Day>, today: Int): Date? {

            //если в расписании нет ни одной timezone, то после рассмотрения 7 дней - выход из рекурсии
            if (++observedDayCount > 7)
                return null

            val nextDay = if (today + 1 < 7) today + 1 else 1

            //если в следующем дне нет timezone
            return if (days[nextDay].timeZones.isEmpty())
                getNextDayFirstTimezone(days, nextDay)
            else{
                val res = getDate(days[nextDay].timeZones[0].from, observedDayCount)
                observedDayCount = 1
                res
            }
        }

        fun getCurrentTimezone(schedule: Schedule): TimeZone?{
            for(timezone in schedule.days[LocalDate.now().dayOfWeek.ordinal].timeZones){
                if (TimezoneUtil.isCurrentTimeInTimezone(timezone.from, timezone.to)) {
                    return timezone
                }
            }
            return null
        }

        fun isCurrentTimeInTimezone(from: String, to: String): Boolean {
            val timezoneStartTime = LocalTime.parse(transformToIsoTime(from), DateTimeFormatter.ISO_TIME)
            val timezoneFinishTime = LocalTime.parse(transformToIsoTime(to), DateTimeFormatter.ISO_TIME)
            val currentTime = LocalDateTime.now().toLocalTime()

            return currentTime.isAfter(timezoneStartTime) && currentTime.isBefore(timezoneFinishTime)
        }

        fun sortTimeZones(timeZones: ArrayList<TimeZone>): ArrayList<TimeZone> {
            timeZones.sortWith(TimeZoneComparator())
            return timeZones
        }

        private fun getDate(time: String, daysToAdd: Int): Date {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, daysToAdd)
            val date = cal.time
            date.hours = time.split(":")[0].toInt()
            date.minutes = time.split(":")[1].toInt()
            date.seconds = 0
            return date
        }

        //если час состояит из одной цифры, то добавляем 0 в начало
        private fun transformToIsoTime(time: String): String{
            if (time.split(":")[0].length == 1)
                return "0$time"
            return time
        }

        private class TimeZoneComparator: Comparator<TimeZone>{

            override fun compare(timeZone1: TimeZone, timeZone2: TimeZone): Int {
                try {
                    val from1 = timeZone1.from.split(":")
                    val from2 = timeZone2.from.split(":")
                    return when {
                        from1[0].toInt() > from2[0].toInt() -> 1
                        from1[0].toInt() < from2[0].toInt() -> -1
                        else -> {
                            when {
                                from1[1].toInt() > from2[1].toInt() -> 1
                                from1[1].toInt() < from2[1].toInt() -> -1
                                else -> 0
                            }
                        }
                    }
                }catch (e: java.lang.Exception){
                    return 0
                }
            }

        }
    }
}