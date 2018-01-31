package com.zhph.common.kafka.constants;

public class Constants {
	public static final String BUNDLE_KEY = "messages";
	public static final String FILE_SEP = System.getProperty("file.separator");
	public static final String WEB_CONFIG = "webConfig";
	public static final String CSS_THEME = "csstheme";
	public static final String CSS_SUFFIX = "csssuffix";
	public static final String JS_SUFFIX = "jssuffix";
	public static final String NEW_FORM = "newForm";
	public static final String I18N_LANGUAGE = "language";
	public static String DATE_FORMATE = "yyyy-MM-dd";
	public static String DATE_TIME_FORMATE = "yyyy-MM-dd HH:mm:ss";
	public static final String MESSAGE_INFO = "info";
	public static final String MESSAGE_ERROR = "error";
	public static final String MESSAGE_WARNING = "warning";
	public static final String MESSAGE_SUCCESS = "success";
	public static final String MESSAGE_INFO_FOOTER = "infoFooter";
	public static final String MESSAGE_ERROR_FOOTER = "errorFooter";
	public static final String MESSAGE_WARNING_FOOTER = "warningFooter";
	public static final String MESSAGE_SUCCESS_FOOTER = "successFooter";
	public static final String MESSAGE_ACTION = "action";
	public static final String MESSAGE_VALIDATE_FAIL = "param_validate_fail";
	
	// -------loggerName key-s----------
	public static final String LOGGERNAME_RECHARGE = "recharge";
	public static final String LOGGERNAME_PURCHASE = "purchase";
	public static final String LOGGERNAME_BLOCKTRADING = "blocktrading";
	public static final String LOGGERNAME_REDEMPTION = "redemption";
	public static final String LOGGERNAME_WITHDRAW = "withdraw";
	// -------loggerName key-s----------
	public static final Integer REDIS_CACHETIME = 60*2;//邮箱验证码 短信验证码时间
	//public static final String TRANS_RETRY_COUNT_TOPIC_POSTFIX = "retryCountTopic"; //事务最终一致性消费者发送的重试次数消息主题后缀
	public static final String TRANS_REPLY_TOPIC = "MsgReply.SUCCESS"; //设置消息状态的TOPIC
}
