public class CourseScheduler {
    private static void  sortIntervals(int[][] intervals) {
        for (int i = 0; i < intervals.length - 1; i++) {
            for (int j = i + 1; j < intervals.length; j++) {
                if (intervals[i][1] > intervals[j][1]) {
                    int[] temp = intervals[i];
                    intervals[i] = intervals[j];
                }
            }
        }
    }
    public static int maxNonOverlappingCourses(int[][] courses) {
        sortIntervals(courses);

        int count = 0;
        int lastEndTime = -1;
        for (int i = 0; i < courses.length; i++) {
            int startTime = courses[i][0];
            int endTime = courses[i][1];
            if (startTime >= lastEndTime) {
                count++;
                lastEndTime = endTime;
            }
        }
        return count;
    }
}
