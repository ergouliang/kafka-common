package com.zhph.common.kafka.exception;

public class CodeConst {
	
	public static final String SERVICE_ERR_CODE_DEFAULT="SRV_ERR_001";//服务层默认异常
	
	public static final String DAO_ERR_CODE_DEFAULT="DAO_ERR_001";//dao层默认异常
	/**系统管理异常***/
	public static final String SYS_ROLE_EXIST_ERR="SYS_ROLE_ROLENAME_EXIST";//角色已经存在
	public static final String SYS_USERNO_EXIST_ERR="SYS_USERNO_EXIST";//用户已经存在
	public static final String BRANCH_INNERNO_EXIST = "BRANCH_INNERNO_EXIST"; //机构号已经存在
	
	/***登陆**/
	public static final String USER_NOT_FOUND="SRV_ERR_USER_001";//找不到指定用户	
	public static final String USER_PASSWORD_ERROR="USER_PASSWORD_ERROR";//找不到指定用户	
	public static final String USER_PASSWORD_INEFFECT="USER_PASSWORD_INEFFECT_ERROR";//密码失效
	public static final String USER_UNEFFECTED_ERR="USER_UNEFFECTED_ERR";//无效用户
	
	/**userCenterService 用户中心异常*/
	public static final String USERCENTER_XXXX_ERROR="USERCENTER_XXXX_ERROR";//用户中心xxx服务异常


	/**wuliuService 物流平台异常*/
	
	public static final String WULIU_XXXX_ERROR = "WULIU_XXXX_ERROR"; // 物流xxx服务异常

	
	/**saleService 贸易平台异常*/
	public static final String SALE_XXX_ERROR = "SALE_XXX_ERROR";//贸易平台xxx服务异常

	
	/**comsumerService 消费异常*/
	public static final String COMSUMER_XXX_ERROR = "COMSUMER_XXX_ERROR";//消费平台xxx服务异常
	
	/** productService 生产平台异常*/
	public static final String PRODUCT_XXX_ERROR = "PRODUCT_xxx_ERROR";//生产平台xxx服务异常

}
