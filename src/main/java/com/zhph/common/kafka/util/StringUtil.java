package com.zhph.common.kafka.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：String 工具类
 * 
 * @author heyin
 * @version 1.0 2016-03-22
 */
public class StringUtil {

	/**
	 * 构造函数
	 */
	public StringUtil() {
	}

	/**
	 * 比较两个字符串是否相等，null和“”等同
	 * 
	 * @param strA
	 *            String
	 * @param strB
	 *            String
	 * @return boolean (true:相等 false:不相等)
	 */
	public static boolean equals(String strA, String strB) {
		if (strA == null || strA.length() == 0) {
			if (strB == null || strB.length() == 0) {
				return true;
			}
			return false;
		}

		return strA.equals(strB);
	}

	/**
	 * 判断字符串是否为null或是长度为0
	 * 
	 * @param str
	 *            String 字符串
	 * @return boolean (true:是 false:否)
	 */
	public static boolean isEmptyOrNull(String str) {
		return Boolean.valueOf(str == null || str.length() == 0);
	}

	/**
	 * 将传入的字符串数组中的所有字符串依次连接成一个字符串并返回
	 * 
	 * @param strs
	 *            要连接的字符串数组
	 * @return 连接后的字符串
	 */
	public static String concatStrings(String... strs) {
		if (strs == null || strs.length == 0) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			sb.append(str);
		}

		return sb.toString();
	}

	/**
	 * 将SQL模糊查询（LIKE）条件中的特殊字符'/', '%', '_'转换成'//', '/%', '/_'
	 * 
	 * @param str
	 *            查询条件字符串
	 * @return 转义后的查询字符串
	 */
	public static String literalizeParam(String str) {
		if (isEmptyOrNull(str)) {
			return str;
		}

//		String[] patterns2BeEscaped = SQLConsts.PATTERNS_2BE_ESCAPED;
//		String[] patternsOfReplacement = SQLConsts.PATTERNS_OF_REPLACEMENT;
		String newStr = str;
//		for (int i = 0; i < patterns2BeEscaped.length; i++) {
//			newStr = newStr.replaceAll(patterns2BeEscaped[i], patternsOfReplacement[i]);
//		}

		return newStr;
	}

	/**
	 * 去除字符串前后的半角和全角空格
	 * 
	 * @param str
	 *            源字符串
	 * @return 去除前后的半角和全角空格后的字符串
	 */
	public static String trim(String str) {
		if (isEmptyOrNull(str)) {
			return str;
		}

		int start = 0;
		int len = str.length();
		int end = len;

		while ((start < len) && (str.charAt(start) == ' ' || str.charAt(start) == '　')) {
			start++;
		}

		while ((start < end) && (str.charAt(end - 1) == ' ' || str.charAt(end - 1) == '　')) {
			end--;
		}

		return ((start > 0) || (end < len)) ? str.substring(start, end) : str;
	}

	/**
	 * 对象转成字符串
	 * 
	 * @param obj
	 *            Object 源对象
	 * @return 返回以后的字符串
	 */
	public static String valueOf(Object obj) {
		return (obj == null) ? "" : obj.toString();
	}

	/**
	 * 用HTML的格式显示多行文字，每一行根据要求截取指定长度的字符(汉、日、韩文字符长度为2), 不区分中英文,如果数字不正好，则少取一个字符位
	 * 
	 * @param aString
	 *            原始字符串
	 * @param specialCharsLength
	 *            截取长度(汉、日、韩文字一个字符长度为2)
	 * @param row
	 *            分割显示行数
	 * @return 分割处理后的字符串List
	 */
	public static String cutString(String aString, int specialCharsLength, int row) {
		List<String> outputStr = subStr(aString, specialCharsLength, row);
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		for (int i = 0; i < outputStr.size(); i++) {
			sb.append(outputStr.get(i));
			sb.append("<br>");
		}
		sb.append("</html>");
		return sb.toString();
	}

	/**
	 * 截取一段字符的长度(汉、日、韩文字符长度为2),不区分中英文,如果数字不正好，则少取一个字符位
	 * 
	 * @param str
	 *            原始字符串
	 * @param specialCharsLength
	 *            截取长度(汉、日、韩文字符长度为2)
	 * @param row
	 *            分割显示行数
	 * @return 分割处理后的字符串List
	 */
	private static List<String> subStr(String str, int specialCharsLength, int row) {
		if (str == null || "".equals(str) || specialCharsLength < 1) {
			return null;
		}
		List<String> strList = new ArrayList<String>();
		char[] chars = str.toCharArray();
		int tempLength = 0;
		for (int i = 0; i < row; i++) {
			int charsLength = getCharsLength(chars, specialCharsLength, tempLength);
			strList.add(new String(chars, tempLength, charsLength));
			tempLength = tempLength + charsLength;
			if (tempLength >= str.length()) {
				break;
			}
		}
		return strList;
	}

	/**
	 * 获取一段字符的长度，输入长度中汉、日、韩文字符长度为2，输出长度中所有字符均长度为1
	 * 
	 * @param chars
	 *            一段字符
	 * @param specialCharsLength
	 *            输入长度，汉、日、韩文字符长度为2
	 * @param tempLength
	 *            int
	 * @return 输出长度，所有字符均长度为1
	 */
	private static int getCharsLength(char[] chars, int specialCharsLength, int tempLength) {
		int count = 0;
		int normalCharsLength = 0;
		for (int i = tempLength; i < chars.length; i++) {
			int specialCharLength = getSpecialCharLength(chars[i]);
			if (count <= specialCharsLength - specialCharLength) {
				count += specialCharLength;
				normalCharsLength++;
			} else {
				break;
			}
		}
		return normalCharsLength;
	}

	/**
	 * 获取字符长度：汉、日、韩文字符长度为2，ASCII码等字符长度为1
	 * 
	 * @param c
	 *            字符
	 * @return 字符长度
	 */
	private static int getSpecialCharLength(char c) {
		if (isLetter(c)) {
			return 1;
		} else {
			return 2;
		}
	}

	/**
	 * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
	 * 
	 * @param c
	 *            char, 需要判断的字符
	 * @return boolean (true:Ascill字符 false:其它字符)
	 */
	private static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}

	/**
	 * 判断一个字符串所有字符全是Ascill字符
	 * 
	 * @param str
	 *            String, 需要判断的字符
	 * @return boolean (true:全是Ascill字符 false:有其它字符)
	 */
	public static boolean isLetter(String str) {
		if (!isEmptyOrNull(str)) {
			char[] chars = str.toCharArray();
			for (char c : chars) {
				int k = 0x80;
				if (c / k != 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 判断当前字符str是否包含specialStr(、、：*？“《》|\\/:?\"<>)等特殊字符
	 * 
	 * @param str
	 *            String
	 * @param specialStr
	 *            String
	 * @return 如果包含了就返回TRUE，否则返回false
	 */
	public static boolean isExistSpecialStr(String str, String specialStr) {

		if (!StringUtil.isEmptyOrNull(str) && !StringUtil.isEmptyOrNull(specialStr)) {
			for (int j = 0; j < specialStr.length(); j++) {

				if (str.indexOf(specialStr.charAt(j)) != -1) { // 如果当前字符存在specialStr所包含的字符中
					return true;
					// 特殊字符不存在
				} else {
				}
			}
			return false;
		} else {
			return false;
		}
	}

	private static Random randGen = new Random();

	private static char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	public static boolean isNullorBlank(String inputstring) {
		return (inputstring == null) || (inputstring.trim().equals(""));
	}

	public static boolean notNullorBlank(String inputstring) {
		return (inputstring != null) && (!inputstring.trim().equals(""));
	}

	public static String filteringComma(String comma) {
		if (comma.endsWith(",")) {
			comma = comma.substring(0, comma.length() - 1);
		}
		return comma;
	}

	/**
	 * 
	 * @Title: getSimpleDateFormat @Description: TODO(统一日期格式对象) @param @param
	 * formatter @param @return 设定文件 @return SimpleDateFormat 返回类型 @throws
	 */
	public static SimpleDateFormat getSimpleDateFormat(String formatter) {
		SimpleDateFormat ft = new SimpleDateFormat(formatter);
		// System.setProperty("user.timezone","Asia/Shanghai");
		ft.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		return ft;
	}

	public static boolean isNumeric(String tmpstr) {
		try {
			Double.parseDouble(tmpstr);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static String formatNumber(double forNum, int intDecLength) {
		if (intDecLength <= 0) {
			return forNum + "";
		}
		String formatExpress = "#.";
		for (int i = 1; i <= intDecLength; ++i) {
			formatExpress = formatExpress + "0";
		}

		return new DecimalFormat(formatExpress).format(forNum);
	}

	public static String formatNumber2(double forNum, int intDecLength) {
		return formatNumber(forNum * 100.0D, intDecLength) + "%";
	}

	public static String formatDate(Date forDate, String formatString) {
		if (forDate == null) {
			return "";
		}
		SimpleDateFormat formatter = getSimpleDateFormat(formatString);
		String strDate = formatter.format(forDate);
		return strDate;
	}

	public static String formatDateyyyyMMdd(Date forDate) {
		return formatDate(forDate, "yyyy-MM-dd");
	}

	public static String formatDateZHyyyyMMdd(Date forDate) {
		return formatDate(forDate, "yyyy年MM月dd日");
	}

	public static String formatDateyyyyMMddHHmmss(Date forDate) {
		return formatDate(forDate, "yyyy-MM-dd HH:mm:ss");
	}

	public static String formatDate4(Date forDate) {
		return formatDate(forDate, "yyyy-MM-dd HH:mm:ss.SSS");
	}

	public static Date stringToDate(String str, String formatString) throws ParseException {
		if (str == null)
			return null;
		DateFormat df = getSimpleDateFormat(formatString);

		Date d1 = df.parse(str);
		return d1;
	}

	public static Date stringToDate1(String str) throws ParseException {
		if (str.length() > 10) {
			if (str.indexOf(".") > 0) {
				return stringToDate4(str);
			}
			return stringToDate3(str);
		}

		return stringToDate2(str);
	}

	public static Date stringToDate2(String str) throws ParseException {
		return stringToDate(str, "yyyy-MM-dd");
	}

	public static Date stringToDate3(String str) throws ParseException {
		return stringToDate(str, "yyyy-MM-dd HH:mm:ss");
	}

	public static Date stringToDate4(String str) throws ParseException {
		return stringToDate(str, "yyyy-MM-dd HH:mm:ss.SSS");
	}

	public static BigDecimal stringToDecimal(String str) throws Exception {
		if (str == null) {
			return null;
		}
		BigDecimal dc = new BigDecimal(str);
		return dc;
	}

	public static String getLastString(String src, String splitString) {
		if ((src == null) || (src.trim().equals(""))) {
			return "";
		}
		String[] ary_tmp = src.split("[" + splitString + "]");
		return ary_tmp[(ary_tmp.length - 1)];
	}

	public static String stringArrayToString(String[] arrayString, String splitStr, Boolean noComma) {
		if (arrayString == null)
			return null;
		if (arrayString.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arrayString.length; ++i) {
			if (!isNullorBlank(arrayString[i]))
				if (noComma == null) {
					sb.append("'" + arrayString[i].trim() + "'");
				} else {
					sb.append(arrayString[i].trim());
				}
			else {
				sb.append("");
			}
			if (i < arrayString.length - 1)
				sb.append(splitStr);
		}
		return sb.toString();
	}

	public static String stringArrayToString(String[] arrayString, String splitStr) {
		return stringArrayToString(arrayString, splitStr, null);
	}

	public static String[] strObjToStrArray(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof String[]) {
			return (String[]) obj;
		}
		return new String[] { obj.toString() };
	}

	public static String replaceChar(String input, Map<Character, String> map) {
		if (isNullorBlank(input))
			return "";
		if ((map == null) || (map.size() == 0))
			return input;
		StringBuffer sb = new StringBuffer();
		char[] chars = input.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			Character tmpchar = new Character(chars[i]);
			if (map.containsKey(tmpchar))
				sb.append((String) map.get(tmpchar));
			else {
				sb.append(tmpchar);
			}
		}
		return sb.toString();
	}

	/**
	 * 返回指定日期是哪一月
	 */
	public static int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(2) + 1;
	}

	/**
	 * 返回指定日期是哪一年
	 */
	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(1);
	}

	/**
	 * 返回指定日期是哪一天
	 */
	public static int getDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(5);
	}

	/**
	 * 用户ID转换 1,2,3,4,5 变成'1','2','3','4','5'
	 * 
	 * @param userIds
	 * @return
	 */
	public String userIdsTransform(String input) {
		StringBuffer idf = new StringBuffer();
		String[] ids = input.split(",");
		int len = ids.length;
		for (int i = 0; i < len; i++) {
			idf.append("'" + ids[i] + "'");
			if (i < len - 1) {
				idf.append(",");
			}
		}

		return idf.toString();
	}

	public static StringBuilder insertString(String s1, String s2, int l) {
		StringBuilder sb = new StringBuilder();
		sb.append(s1).insert(l, s2);
		return sb;
	}

	public static String clobToString(Clob clob) throws IOException, SQLException {
		if (clob == null) {
			return "";
		}
		return readerToString(clob.getCharacterStream());
	}

	/**
	 * Reader流变字符串 <br>
	 * 主要处理带中文的文件
	 * 
	 * @param read
	 * @return
	 * @throws IOException
	 */
	public static String readerToString(Reader read) throws IOException {
		IOException err = null;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(read);

		int j = 0;
		try {
			String i;
			while ((i = br.readLine()) != null) {
				if (j > 0)
					sb.append("\r\n");
				sb.append(i);
				++j;
			}
		} catch (IOException e) {
			e.printStackTrace();
			err = e;
		}
		try {
			br.close();
		} catch (IOException er) {
			er.printStackTrace();
			err = er;
		}
		if (err != null)
			throw err;
		return sb.toString();
	}

	/**
	 * 格式化字符串内的变量
	 * 
	 * @param strVar
	 *            String
	 * @param args
	 *            String[]
	 * @return String
	 */
	public static String format(String strVar, String[] args) {
		String str = strVar;
		if (str.indexOf("%s") != -1) {
			str = formatDep(str, args);
		}
		return MessageFormat.format(str, (Object[]) args);
	}

	/**
	 * 格式化字符串内的变量
	 * 
	 * @param strVar
	 *            String
	 * @param args
	 *            String[]
	 * @return String
	 */
	private static String formatDep(String strVar, String[] args) {
		String str = strVar;
		if (args == null) {
			return str;
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null && str.indexOf("%s") != -1) {
				str = str.replaceFirst("\\%s", args[i]);
			}
		}

		return str;
	}

	/**
	 * @deprecated Method format is deprecated
	 * @param str
	 *            String
	 * @param arg
	 *            String
	 * @return String
	 */
	public static String format(String str, String arg) {
		return format(str, new String[] { arg });
	}

	/**
	 * @deprecated Method format is deprecated
	 * @param str
	 *            String
	 * @param arg1
	 *            String
	 * @param arg2
	 *            String
	 * @return String
	 */
	public static String format(String str, String arg1, String arg2) {
		return format(str, new String[] { arg1, arg2 });
	}

	/**
	 * @deprecated Method format is deprecated
	 * @param str
	 *            String
	 * @param arg1
	 *            String
	 * @param arg2
	 *            String
	 * @param arg3
	 *            String
	 * @return String
	 */
	public static String format(String str, String arg1, String arg2, String arg3) {
		return format(str, new String[] { arg1, arg2, arg3 });
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param strVar
	 *            String
	 * @return boolean
	 */
	public static boolean isBlank(String strVar) {
		String str = strVar;
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串是否不为空
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * 格式化字符串为Java规则命名
	 * 
	 * @param name
	 *            String
	 * @param firstCharUpperCase
	 *            boolean
	 * @return String
	 */
	public static final String formatJavaName(String name, boolean firstCharUpperCase) {
		if (name == null || name.length() <= 1) {
			return name;
		}
		StringTokenizer tokenizer = new StringTokenizer(name, "-_");
		StringBuffer tmp = new StringBuffer();
		String token;
		for (; tokenizer.hasMoreTokens(); tmp.append(Character.toUpperCase(token.charAt(0)))
				.append(token.substring(1))) {
			token = tokenizer.nextToken();
		}

		if (!firstCharUpperCase) {
			String ch = String.valueOf(Character.toLowerCase(tmp.charAt(0)));
			tmp.replace(0, 1, ch);
		}
		return tmp.toString();
	}

	/**
	 * 格式化字符串数组变成字符串‘1’，‘2’，‘3’
	 * 
	 * @param name
	 *            String
	 * @param firstCharUpperCase
	 *            boolean
	 * @return String
	 */
	public static final String formatStringArrayToStr(String[] strs) {
		if (strs == null) {
			return "";
		}
		if (strs.length == 0) {
			return "";
		}
		StringBuffer tmp = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			tmp.append("'" + strs[i] + "'");
			if (i < strs.length - 1) {
				tmp.append(",");
			}
		}
		return tmp.toString();
	}

	/**
	 * 格式化字符串为Java规则命名
	 * 
	 * @param name
	 *            String
	 * @return String
	 */
	public static final String formatJavaName(String name) {
		return formatJavaName(name, false);
	}

	private static final Pattern humpPattern = Pattern.compile("[A-Z]");

	/**
	 * 驼峰转下划线
	 * 
	 */
	public static String humpToLine(String str) {
		Matcher matcher = humpPattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(sb);
		// if("_".equals(sb.charAt(0)+""))
		// sb.delete(0, 1);
		return sb.toString();
	}

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		return str.matches("[\\d]+[.]?[\\d]+");
	}

	/**
	 * 判断是否为合法IP * @return true or false
	 */
	public static boolean isIpv4(String ipAddress) {
		String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	/**
	 * 拼接多个字符串
	 * 
	 * @param strs
	 *            字符串数组
	 * @return
	 */
	public static String append(Object... strs) {
		return append(new StringBuilder(), strs);
	}

	/**
	 * 分割字符串
	 * 
	 * @author 母德亮
	 * @date 2017年4月6日 上午11:14:38
	 * @param str
	 * @param size
	 * @param fix
	 *            是否进行修正,包含不齐的分割 0108 3 true返回 [010,0108],false返回 [010]
	 * @return List<String>
	 */
	public static List<String> split(String str, int size, boolean fix) {
		List<String> list = new ArrayList<String>();
		int len = str.length() / size;
		for (int i = 0; i < len; i++) {
			list.add(str.substring(0, (i + 1) * size));
		}
		if (fix && str.length() % size != 0)
			list.add(str);
		return list;
	}

	/**
	 * 拼接多个字符串
	 * 
	 * @param sb
	 *            容器
	 * @param strs
	 *            字符串数组
	 * @return
	 */
	public static String append(StringBuilder sb, Object... strs) {
		for (Object str : strs) {
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * 获取国际化里面的值
	 * 
	 * @param key
	 * @return
	 */
	public static String getI18NValue(String key) {
//		try {
//			return SpringContextHolder.getApplicationContext().getMessage(key, null, Locale.getDefault());
//		} catch (NoSuchMessageException e) {
			return null;
//		}
	}

	/**
	 * 将异常堆栈转换为字符串
	 * 
	 * @param aThrowable
	 *            异常
	 * @return String
	 */
	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	public static final String EMPTY_STRING = "";
	public static final char DOT = 46;
	public static final char UNDERSCORE = 95;
	public static final String COMMA_SPACE = ", ";
	public static final String COMMA = ",";
	public static final String OPEN_PAREN = "(";
	public static final String CLOSE_PAREN = ")";
	public static final String EMPTY = "";

}
