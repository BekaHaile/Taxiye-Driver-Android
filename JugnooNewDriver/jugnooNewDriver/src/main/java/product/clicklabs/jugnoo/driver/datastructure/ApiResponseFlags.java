package product.clicklabs.jugnoo.driver.datastructure;

public enum ApiResponseFlags {
	
	PARAMETER_MISSING(100),
	INVALID_ACCESS_TOKEN(101),
	ERROR_IN_EXECUTION(102),
	SHOW_ERROR_MESSAGE(103),
	SHOW_MESSAGE(104),
	
	ASSIGNING_DRIVERS(105),
	NO_DRIVERS_AVAILABLE(106),
	RIDE_ACCEPTED(107),
	RIDE_ACCEPTED_BY_OTHER_DRIVER(108),
	RIDE_CANCELLED_BY_DRIVER(109),
	REQUEST_REJECTED(110),
	REQUEST_TIMEOUT(111),
	REQUEST_CANCELLED(112),
	SESSION_TIMEOUT(113),
	RIDE_STARTED(114),
	RIDE_ENDED(115),
	WAITING(116),
    RIDE_CANCELLED_BY_CUSTOMER(118),
	
	USER_OFFLINE(130),
	NO_ACTIVE_SESSION(131),
	ENGAGEMENT_DATA(132),
	ACTIVE_REQUESTS(133),
	LAST_ENGAGEMENT_DATA(135),
    RIDE_PATH_RECEIVED(136),
	
	COUPONS(140),
	SCHEDULED_PICKUPS(141),
	ACK_RECEIVED(142),
	
	ACTION_COMPLETE(143),
	ACTION_FAILED(144),
	DRIVER_TIMEOUT(177),
	
	LOGIN_SUCCESSFUL(150),
	INCORRECT_PASSWORD(151),
	CUSTOMER_LOGGING_IN(152),
	LOGOUT_SUCCESSFUL(153),
	LOGOUT_FAILURE(154),
	NO_SUCH_USER(155),
	
	


	//new authentication server flags
	AUTH_DUPLICATE_REGISTRATION(400),
	AUTH_REGISTRATION_SUCCESSFUL(401),
	AUTH_REGISTRATION_FAILURE(402),
	AUTH_ALREADY_REGISTERED(403),
	AUTH_NOT_REGISTERED(404),
	AUTH_VERIFICATION_REQUIRED(405),
	AUTH_VERIFICATION_FAILURE(406),
	AUTH_LOGIN_SUCCESSFUL(407),
	AUTH_LOGIN_FAILURE(408),
	AUTH_LOGOUT_SUCCESSFUL(409),
	AUTH_LOGOUT_FAILURE(410),
	TRANSACTION_HISTORY(423),



	HEATMAP_DATA(530),
	DISTANCE_RESET(555),


	RESET_DEVICE_TOKEN(556),
	UPLOAD_DOCCUMENT(701),
	UPLOAD_DOCUMENT_REFRESH(702),
	TNC_NOT_ACCEPTED(712),

	EMERGENCY_CONTACTS(450),
	;

	private int ordinal;

	ApiResponseFlags(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}
	public int getKOrdinal() {
		return ordinal;
	}

}
