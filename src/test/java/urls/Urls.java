package urls;

public abstract class Urls {
    public static final String URL =  "https://qa-scooter.praktikum-services.ru/api/v1";
    public static final String ORDERS = "/orders";
    public static final String PUT_ORDER = "/orders/accept/{orderId}";
    public static final String INVALID_PUT_ORDER = "/orders/accept";
    public static final String GET_ORDER = "/orders/track";
    public static final String POST_COURIER = "/courier";
    public static final String LOGIN_COURIER = "/courier/login";
    public static final String DELETE_COURIER = "/courier/{courierId}";
}
