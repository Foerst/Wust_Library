package com.chen.library.HttpPath;

public class UrlPath {
	public static String WUST_LIB_HOME_URL = "http://opac.lib.wust.edu.cn:8080/opac/";
	public static String PAGE_URL1 = "http://opac.lib.wust.edu.cn:8080/opac/openlink.php?dept=ALL&title=";
	public static String PAGE_URL2 = "&doctype=ALL&lang_code=ALL&match_flag=forward&displaypg=20&showmode=list&orderby=DESC&sort=CATA_DATE&onlylendable=no&with_ebook=&page=";
	public static String LOGIN_URL = "http://opac.lib.wust.edu.cn:8080/reader/login.php";
	public static String VERIFY_IMAGE_URL = "http://opac.lib.wust.edu.cn:8080/reader/captcha.php?code=";

	public static String DOUBAN_IMAGE_URL = "http://api.douban.com/book/subject/isbn/";
	public static String DOUBAN_BOOK_INFO_WITH_ISBN="https://api.douban.com/v2/book/isbn/";
	public static String IMAGE_URL;//
	public static String SUMMARY_OF_BOOK;//
	public static String CURRENT_AFFAIR_URL = "http://www.lib.wust.edu.cn/%E6%8A%A5%E5%88%8A%E6%96%87%E8%90%83/bklist.aspx?type=%CA%B1%CA%C2";
	public static String SOCIETY_URL = "http://www.lib.wust.edu.cn/%E6%8A%A5%E5%88%8A%E6%96%87%E8%90%83/bklist.aspx?type=%C9%E7%BB%E1";
	public static String EDUCATION_URL = "http://www.lib.wust.edu.cn/%E6%8A%A5%E5%88%8A%E6%96%87%E8%90%83/bklist.aspx?type=%BD%CC%D3%FD";
	public static String ECONOMY_URL = "http://www.lib.wust.edu.cn/%E6%8A%A5%E5%88%8A%E6%96%87%E8%90%83/bklist.aspx?type=%BE%AD%BC%C3";
	public static String MILITARY_URL = "http://www.lib.wust.edu.cn/%E6%8A%A5%E5%88%8A%E6%96%87%E8%90%83/bklist.aspx?type=%BE%FC%CA%C2";
	// 证件信息
	public static String REDR_INFO_URL = "http://opac.lib.wust.edu.cn:8080/reader/redr_info.php";
	// 借阅历史
	public static String BOOK_HIST_URL = "http://opac.lib.wust.edu.cn:8080/reader/book_hist.php";
	//当前借阅
	public static String BOOK_LST_URL = "http://opac.lib.wust.edu.cn:8080/reader/book_lst.php";
}
