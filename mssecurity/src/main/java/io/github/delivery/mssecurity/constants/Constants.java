package io.github.delivery.mssecurity.constants;

public class Constants {

    private Constants() {
    }

    public static final String ROLE_PREFIX = "ROLE_";

    public static final String ADMIN = "ADMIN";
    public static final String SUPPLIER = "SUPPLIER";
    public static final String CLIENT = "CLIENT";

    public static final String IS_ADMIN = "hasRole('ADMIN')";
    public static final String IS_SUPPLIER = "hasRole('SUPPLIER')";
    public static final String IS_CLIENT = "hasRole('CLIENT')";
    public static final String IS_ADMIN_OR_SUPPLIER = "hasRole('ADMIN') or hasRole('SUPPLIER')";
}
