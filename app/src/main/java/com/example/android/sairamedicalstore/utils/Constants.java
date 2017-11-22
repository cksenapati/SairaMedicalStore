package com.example.android.sairamedicalstore.utils;

/**
 * Created by chandan on 01-08-2017.
 */

public final class Constants {

    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_UPDATE = "timestampLastUpdate";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_CREATED = "timestampCreated";

    public static final String FIREBASE_PROPERTY_DEFAULT_IMAGE_URL = "defaultImageURL";
    public static final String FIREBASE_PROPERTY_MEDICINE_NAME = "medicineName";
    public static final String FIREBASE_PROPERTY_MANUFACTURER_NAME = "manufacturerName";
    public static final String FIREBASE_PROPERTY_COMPOSITION_NAME = "compositionName";
    public static final String FIREBASE_PROPERTY_DISPLAY_CATEGORY_NAME = "displayCategoryName";
    public static final String FIREBASE_PROPERTY_CATEGORY_NAME = "categoryName";
    public static final String FIREBASE_PROPERTY_TYPE_NAME = "typeName";
    public static final String FIREBASE_PROPERTY_POSTER_NAME = "posterName";
    public static final String FIREBASE_PROPERTY_PIC_URL = "picUrl";
    public static final String FIREBASE_PROPERTY_DEFAULT_MEDICINE_PICS = "defaultMedicinePics";
    public static final String FIREBASE_PROPERTY_COMMON_DEFAULT_VALUES = "commonDefaultValues";
    public static final String FIREBASE_PROPERTY_ORDER_ID = "orderId";


    public static final String FIREBASE_LOCATION_ALL_USERS = "allUsers";
    public static final String FIREBASE_LOCATION_All_MEDICINES = "allMedicines";
    public static final String FIREBASE_LOCATION_All_MEDICINE_CATEGORIES = "allMedicineCategories";
    public static final String FIREBASE_LOCATION_All_MEDICINE_TYPES = "allMedicineTypes";
    public static final String FIREBASE_LOCATION_All_DISPLAY_CATEGORIES = "allDisplayCategories";
    public static final String FIREBASE_LOCATION_All_DISPLAY = "allDisplay";
    public static final String FIREBASE_LOCATION_All_DEFAULT_VALUES = "allDefaultValues";
    public static final String FIREBASE_LOCATION_All_MANUFACTURERS = "allManufacturers";
    public static final String FIREBASE_LOCATION_All_COMPOSITIONS = "allCompositions";
    public static final String FIREBASE_LOCATION_All_OFFERS = "allOffers";
    public static final String FIREBASE_LOCATION_All_POSTERS = "allPosters";
    public static final String FIREBASE_LOCATION_All_CARTS = "allCarts";
    public static final String FIREBASE_LOCATION_All_PRESCRIPTIONS = "allPrescriptions";
    public static final String FIREBASE_LOCATION_All_ADDRESSES = "allAddresses";
    public static final String FIREBASE_LOCATION_All_ORDERS = "allOrders";
    public static final String FIREBASE_LOCATION_All_PROFILE_PICS = "allProfilePics";




    public static final String FIREBASE_URL = "https://sairamedicalstore-4b9c2.firebaseio.com/";
    public static final String FIREBASE_URL_SAIRA_ALL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_ALL_USERS;
    public static final String FIREBASE_URL_SAIRA_ALL_MEDICINES = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_MEDICINES;
    public static final String FIREBASE_URL_SAIRA_All_MEDICINE_CATEGORIES = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_MEDICINE_CATEGORIES;
    public static final String FIREBASE_URL_SAIRA_All_MEDICINE_TYPES = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_MEDICINE_TYPES;
    public static final String FIREBASE_URL_SAIRA_All_DISPLAY_CATEGORIES = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_DISPLAY_CATEGORIES;
    public static final String FIREBASE_URL_SAIRA_All_DISPLAY = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_DISPLAY;
    public static final String FIREBASE_URL_SAIRA_All_DEFAULT_VALUES = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_DEFAULT_VALUES;
    public static final String FIREBASE_URL_SAIRA_All_MANUFACTURERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_MANUFACTURERS;
    public static final String FIREBASE_URL_SAIRA_All_COMPOSITIONS = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_COMPOSITIONS;
    public static final String FIREBASE_URL_SAIRA_All_OFFERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_OFFERS;
    public static final String FIREBASE_URL_SAIRA_All_POSTERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_POSTERS;
    public static final String FIREBASE_URL_SAIRA_All_CARTS = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_CARTS;
    public static final String FIREBASE_URL_SAIRA_All_PRESCRIPTIONS = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_PRESCRIPTIONS;
    public static final String FIREBASE_URL_SAIRA_All_ADDRESSES = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_ADDRESSES;
    public static final String FIREBASE_URL_SAIRA_All_ORDERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_ORDERS;



    public static final String USER_TYPE_END_USER = "endUser";
    public static final String USER_TYPE_DEVELOPER = "developer";
    public static final String USER_TYPE_EMPLOYEE = "employee";
    public static final String USER_TYPE_OWNER = "owner";

    public static final String MEDICINE_CATEGORY_PRESCRIPTION = "Prescription";
    public static final String MEDICINE_CATEGORY_OTC = "Over the counter";

    public static final String MEDICINE_TYPE_CAPSULE = "Capsule";
    public static final String MEDICINE_TYPE_TABLET = "Tablet";
    public static final String MEDICINE_TYPE_SYRUP = "Syrup";
    public static final String MEDICINE_TYPE_INJECTION = "Injection";

    public static final String SEARCH_MEDICINE = "searchMedicine";
    public static final String SEARCH_MEDICINE_MANUFACTURER = "searchMedicineManufacturer";
    public static final String SEARCH_MEDICINE_COMPOSITION = "searchMedicineComposition";
    public static final String SEARCH_DISPLAY_CATEGORY = "searchDisplayCategory";
    public static final String SEARCH_POSTER = "searchPoster";
    public static final String SEARCH_ORDER = "searchOrder";


    public static final String UPLOAD_SUCCESSFUL = "Upload successful";
    public static final String UPDATE_SUCCESSFUL = "Update successful";
    public static final String UPLOAD_FAIL = "Upload failed";
    public static final String UPDATE_FAIL = "Update failed";
    public static final String ITEM_ALREADY_EXISTS = "Item already exists..Upload failed";



    public static final String ORDER_STATUS_PLACED = "Order Placed";
    public static final String ORDER_STATUS_DELIVERED = "Delivered";
    public static final String ORDER_STATUS_CANCELED = "Canceled";
    public static final String ORDER_STATUS_UNDER_PROCESS = "Under Process";
    public static final String ORDER_STATUS_REQUESTED_FOR_RETURN = "Requested For Return";
    public static final String ORDER_STATUS_DISPATCHED = "Dispatched";
    public static final String ORDER_STATUS_RETURNED = "Returned";

    public static final String ACTIVITY_TITLE_PROFILE_PIC = "Profile Pic";



}
