import java.util.*;

public class SqlParser {
    private static String SQL = "SELECT  \n" +
            "         EC_CLPS_DV_CD         as   STAFF_GBN,            \n" +
            "         EC_NM            as   STAFF_CLASS,          \n" +
            "         EC_GRP            as   ORG_STAFF_GBN,        \n" +
            "         ODR            as   ORDER_SEQ,            \n" +
            "         ORGZ_ID            as   ORG_ID,\n" +
            "         EMP_NO            as   STAFF_EMP_ID,         \n" +
            "         USE_AYN            as   USE_YN,         \n" +
            "         REG_DTM,                          \n" +
            "         RGR_ID,         \n" +
            "         UPDT_DTM,       \n" +
            "         UTUR_ID\n" +
            "      FROM ETCH005M\n" +
            "         WHERE EC_CLPS_DV_CD IN (\n" +
            "                  SELECT   EC_DV as staff_gbn\n" +
            "                      FROM ETCH004M\n" +
            "                     WHERE (TO_DATE (SCH_REG_DT , 'YYYY-MM-DD')\n" +
            "                               BETWEEN TO_DATE (SYSDATE, 'YYYY-MM-DD')\n" +
            "                                   AND TO_DATE (SYSDATE, 'YYYY-MM-DD')\n" +
            "                           )\n" +
            "                  GROUP BY EC_DV \n" +
            "                  UNION\n" +
            "                  SELECT EC_CLPS_DV_CD as staff_gbn\n" +
            "                    FROM ETCH005M\n" +
            "                   WHERE NVL (USE_AYN, 'Y') <> 'N'   )\n" +
            "      ORDER BY ODR";

    private static final String[] SYNTAX = {
            "SELECT", "DISTINCT", "INSERT", "UPDATE", "DELETE", "FROM", "WHERE",
            "BY", "UNION", "ALL"
    };

    public static void main(String[] args) {
        List<String> strList = new LinkedList<>(Arrays.asList(SQL.split(" ")));
        switch (strList.get(0).toUpperCase()) {
            case "SELECT":
                System.out.printf("method : %s\n\n", strList.get(0).toUpperCase());
                removeUntil(strList, new String[]{"SELECT"});
                if(strList.isEmpty())
                    return;

                String[] strings = getStringsUntil(strList, new String[]{"FROM"}, ",");
                System.out.println("col : ");
                for (String cur : strings)
                    System.out.println(cur.trim());
                System.out.println();

                removeUntil(strList, new String[]{"FROM"});
                if(strList.isEmpty())
                    return;

                strings = getStringsUntil(strList, new String[]{"WHERE"}, ",");
                System.out.println("table :");
                for (String cur : strings)
                    System.out.println(cur.trim());
                System.out.println();

                removeUntil(strList, new String[]{"WHERE"});
                if(strList.isEmpty())
                    return;

                strings = getStringsUntil(strList, new String[]{""}, "AND|OR| ");
                System.out.println("condition :");
                for (String cur : strings)
                    System.out.println(cur.trim());
                System.out.println();
                break;

            default:
                break;
        }
    }

    private static void removeUntil(List<String> strList, String[] criteria) {
        boolean breakFlag = false;
        while (!breakFlag) {
            for (String criterion : criteria) {
                if(strList.isEmpty())
                    return;

                if (strList.get(0).equals(criterion)) {
                    strList.remove(0);
                    breakFlag = true;
                    break;
                } else {
                    strList.remove(0);
                }
            }
        }
    }

    private static String[] getStringsUntil(List<String> strList, String[] criteria, String regex) {
        String[] strings = getStringUntil(getString(strList), criteria).split(regex);
        List<String> totalList = new ArrayList<>();
        List<String> listForCombine = new ArrayList<>();

        Stack<Boolean> bracketStack = new Stack<>();
        for (String cur : strings) {
            char[] iso = cur.toCharArray();
            for (char c : iso) {
                if (c == '(')
                    bracketStack.push(true);
                else if (c == ')')
                    bracketStack.pop();
            }

            listForCombine.add(cur);
            if (bracketStack.empty()) {
                totalList.add(getString(listForCombine));
                listForCombine.clear();
            } else {
                listForCombine.add(regex);
            }
        }

        String[] arr = new String[totalList.size()];
        for (int i = 0; i < totalList.size(); ++i)
            arr[i] = totalList.get(i);
        return arr;
    }

    private static String getString(List<String> strList) {
        StringBuilder builder = new StringBuilder();
        for (String cur : strList)
            builder.append(cur).append(" ");
        return builder.toString().trim();
    }

    private static String getStringUntil(String str, String[] criteria) {
        String[] strings = str.split(" ");
        List<String> untilStrList = new ArrayList<>();
        boolean breakFlag = false;
        for (String cur : strings) {
            for (String criterion : criteria) {
                if (cur.trim().equals(criterion.trim())) {
                    breakFlag = true;
                    break;
                }
            }
            if (breakFlag)
                break;
            untilStrList.add(cur.trim());
        }
        return getString(untilStrList);
    }
}