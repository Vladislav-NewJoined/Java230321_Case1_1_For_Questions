import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ExchangeRates_2 {

    public static void main(String[] args) throws IOException {

// Скачиваем исходной страницы API Центробанка.
        String originalPage = downloadWebPage("https://cbr.ru/scripts/XML_dynamic.asp?date_req1=12/11/2021&date_req2=12/11/2021&VAL_NM_RQ=R01235");
// Задаём адрес исходной страницы API Центробанка в текстовом формате.
        String originalPageText = "https://cbr.ru/scripts/XML_dynamic.asp?date_req1=12/11/2021&date_req2=12/11/2021&VAL_NM_RQ=R01235";

//    Создаем массив ArrayList, с датами месяца.
        List<String> listDates = Arrays.asList("01/03/2023", "02/03/2023", "03/03/2023", "04/03/2023", "05/03/2023", "06/03/2023", "07/03/2023", "08/03/2023", "09/03/2023", "10/03/2023", "11/03/2023", "12/03/2023", "13/03/2023", "14/03/2023", "15/03/2023", "16/03/2023", "17/03/2023", "18/03/2023", "19/03/2023", "20/03/2023", "21/03/2023","22/03/2023", "23/03/2023", "24/03/2023", "25/03/2023", "26/03/2023", "27/03/2023", "28/03/2023", "29/03/2023", "30/03/2023", "31/03/2023");
        int n = listDates.size();
//    Создаем массив ArrayList, куда записываем в качестве элементов курс рубля на текущую дату.
        List<Double> listCourses = new ArrayList<>();

        for (int i = 0; i < listDates.size(); i++) {
            String dtStr = listDates.get(i);
            int startIndex = originalPage.lastIndexOf("<Value>") + 7;
            int endIndex = originalPage.lastIndexOf("</Value>");
            String nextDate;
            // Меняем в адресе исходной страницы дату на следующую.
            String urlWithNextDate = originalPageText.replaceAll("12/11/2021", dtStr);
            String nextPage = downloadWebPage(urlWithNextDate);

            if (nextPage.contains("<Value>")) {
                String courseNextPage = nextPage.substring(startIndex, endIndex);
                // Задаём курс в виде переменной Double.
                double courseNextDoble = Double.parseDouble(courseNextPage.replace(",", "."));
                // System.out.println("Курс в типе переменной Double:");
                // System.out.println(courseNextDoble);
                // Выводим на экран дату и соответствующий курс.
                System.out.println("Курс на " + dtStr + "    " + courseNextDoble);
                listCourses.add(courseNextDoble);
            } else {
                String courseNextPage = "";
                System.out.println("Курс на " + dtStr);
            }
        }

//Далее ищем максимальные перепады курса.
        double max = maxDifference(listCourses);
        double min = minDifference(listCourses);
        DecimalFormat df = new DecimalFormat("0.000");
        df.setRoundingMode(RoundingMode.DOWN);
        System.out.println("\nЗа указанный месяц курс максимально вырос между двумя соседними датами на величину: " + df.format(max));
        System.out.println("За указанный месяц курс максимально упал между двумя соседними датами на величину: " + df.format(min));
    }

//Пишем классы для поиска максимальных перепадов курса.
//Сначала максимальную разницу находим.
    public static double maxDifference(List<Double> listCourses) {
        if (listCourses == null || listCourses.size() == 0) {
            return Double.MIN_VALUE;
        }
        int len = listCourses.size();
        double[] diff = new double[len - 1];
        for (int i = 0; i < len - 1; i++) {
            diff[i] = listCourses.get(i + 1) - listCourses.get(i);
        }
        return max(diff);
    }

    public static double max(double[] diff) {
        if (diff == null || diff.length == 0) {
            return Double.MIN_VALUE;
        }
        double max = diff[0];
        for (int i = 0, len = diff.length; i < len; i++) {
            //not necessary,since 'int[] data' is sorted,so 'int[] diff' is progressively increased.
            //int tmp=diff[i]>0?diff[i]:(-diff[i]);
            if (max < diff[i]) {
                max = diff[i];
            }
        }
        return max;
    }

    //Теперь минимальную разницу находим.
    public static double minDifference(List<Double> listCourses) {
        if (listCourses == null || listCourses.size() == 0) {
            return Double.MIN_VALUE;
        }
        int len = listCourses.size();
        double[] diff = new double[len - 1];
        for (int i = 0; i < len - 1; i++) {
            diff[i] = listCourses.get(i + 1) - listCourses.get(i);
        }
        return min(diff);
    }

    public static double min(double[] diff) {
        if (diff == null || diff.length == 0) {
            return Double.MIN_VALUE;
        }
        double min = diff[0];
        for (int i = 0, len = diff.length; i < len; i++) {
            //not necessary,since 'int[] data' is sorted,so 'int[] diff' is progressively increased.
            //int tmp=diff[i]>0?diff[i]:(-diff[i]);
            if (min > diff[i]) {
                min = diff[i];
            }
        }
        return min;
    }

    private static String downloadWebPage(String url) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;
        URLConnection urlConnection = new URL(url).openConnection();
        try (InputStream is = urlConnection.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }
}