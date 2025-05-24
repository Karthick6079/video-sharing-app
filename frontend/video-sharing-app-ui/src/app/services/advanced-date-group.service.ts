import { Injectable } from "@angular/core";
import { parseISO, differenceInCalendarDays, isToday, isYesterday, getYear, isThisYear, format } from "date-fns";


@Injectable({
    providedIn: 'root'
})
export class AdvancedDateGroupService {

    groupByAdvancedDate<T>(items: T[], dateField: keyof T) {
        const now = new Date();
        const result = new Map<string, T[]>();

        for (const item of items) {
            const rawDate = item[dateField];
            if (!rawDate) continue; // skip if date missing

            let date: Date;

            if (rawDate instanceof Date) {
                date = rawDate;
            } else if (typeof rawDate === 'string') {
                date = parseISO(rawDate);
            } else if (typeof rawDate === 'number') {
                date = new Date(rawDate);
            } else {
                date = new Date(rawDate?.toString?.() ?? '');
            }

            if (isNaN(date.getTime())) continue; //
            const daysAgo = differenceInCalendarDays(now, date);

            let key = '';
            if (isToday(date)) {
                key = 'Today';
            } else if (isYesterday(date)) {
                key = 'Yesterday';
            } else if (daysAgo <= 7) {
                key = 'Previous 7 Days';
            } else if (daysAgo <= 30) {
                key = 'Previous 30 Days';
            } else {
                const year = getYear(date);
                if (isThisYear(date)) {
                    // Group by month like "April 2025"
                    key = format(date, 'MMMM yyyy');
                    //   if (!result[monthLabel]) {
                    //     result[monthLabel] = [];
                    //   }
                    //   result[monthLabel].push(item);
                } else {
                    // Group by year like "2023"
                    key = `${year}`;
                    //   if (!result[yearLabel]) {
                    //     result[yearLabel] = [];
                    //   }
                    //   result[yearLabel].push(item);
                }
            }

            if (!result.has(key)) result.set(key, []);
            result.get(key)?.push(item);
        }
        return result
    }

}