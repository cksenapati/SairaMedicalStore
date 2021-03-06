package com.example.android.sairamedicalstore.utils;

/**
 * Created by chandan on 01-08-2017.
 */

public final class Constants {

    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_UPDATE = "timestampLastUpdate";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_CREATED = "timestampCreated";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_JOINED = "timestampJoined";

    public static final String FIREBASE_PROPERTY_MEDICINE_NAME = "medicineName";
    public static final String FIREBASE_PROPERTY_MEDICINE_TYPE = "medicineType";
    public static final String FIREBASE_PROPERTY_MEDICINE_MANUFACTURER_NAME = "medicineManufacturerName";
    public static final String FIREBASE_PROPERTY_MEDICINE_CATEGORY = "medicineCategory";
    public static final String FIREBASE_PROPERTY_MEDICINE_AVAILABILITY = "medicineAvailability";
    public static final String FIREBASE_PROPERTY_MEDICINE_COMPOSITION = "medicineComposition";

    public static final String FIREBASE_PROPERTY_ORDER_ID = "orderId";
    public static final String FIREBASE_PROPERTY_ORDER_STATUS = "orderStatus";
    public static final String FIREBASE_PROPERTY_ORDER_PAYMENT_METHOD = "paymentMethod";
    public static final String FIREBASE_PROPERTY_ORDER_PLACED_BY = "orderPlaceBy";


    public static final String FIREBASE_PROPERTY_QUERY_ID = "queryId";
    public static final String FIREBASE_PROPERTY_QUERY_STATUS = "queryStatus";
    public static final String FIREBASE_PROPERTY_QUERY_TEXT = "queryText";
    public static final String FIREBASE_PROPERTY_QUERY_TITLE = "queryTitle";
    public static final String FIREBASE_PROPERTY_QUERY_POSTED_BY = "queryPostedBy";


    public static final String FIREBASE_PROPERTY_FAQ_PRIORITY = "faqPriority";
    public static final String FIREBASE_PROPERTY_FAQ_QUESTION = "faqQuestion";

    public static final String FIREBASE_PROPERTY_USER_NAME = "name";
    public static final String FIREBASE_PROPERTY_USER_EMAIL = "email";
    public static final String FIREBASE_PROPERTY_USER_PHONE_NO = "phoneNo";
    public static final String FIREBASE_PROPERTY_USER_TYPE = "userType";

    public static final String FIREBASE_PROPERTY_IS_PRESCRIPTION_SENT_FOR_EVALUATION = "isSentForEvaluation";
    public static final String FIREBASE_PROPERTY_PRESCRIPTION_OWNER_EMAIL = "prescriptionOwnerEmail";



    public static final String FIREBASE_PROPERTY_DEFAULT_IMAGE_URL = "defaultImageURL";
    public static final String FIREBASE_PROPERTY_MANUFACTURER_NAME = "manufacturerName";
    public static final String FIREBASE_PROPERTY_COMPOSITION_NAME = "compositionName";
    public static final String FIREBASE_PROPERTY_DISPLAY_CATEGORY_NAME = "displayCategoryName";
    public static final String FIREBASE_PROPERTY_CATEGORY_NAME = "categoryName";
    public static final String FIREBASE_PROPERTY_TYPE_NAME = "typeName";
    public static final String FIREBASE_PROPERTY_POSTER_NAME = "posterName";
    public static final String FIREBASE_PROPERTY_PIC_URL = "picUrl";
    public static final String FIREBASE_PROPERTY_DEFAULT_MEDICINE_PICS = "defaultMedicinePics";
    public static final String FIREBASE_PROPERTY_COMMON_DEFAULT_VALUES = "commonDefaultValues";
    public static final String FIREBASE_PROPERTY_PIN_CODE = "pinCode";
    public static final String FIREBASE_PROPERTY_DISTRICT = "district";
    public static final String FIREBASE_PROPERTY_STATE = "state";



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
    public static final String FIREBASE_LOCATION_All_QUERIES = "allQueries";
    public static final String FIREBASE_LOCATION_All_FAQS = "allFaqs";
    public static final String FIREBASE_LOCATION_All_PRESCRIPTIONS_TO_BE_EVALUATED = "allPrescriptionsToBeEvaluated";
    public static final String FIREBASE_LOCATION_All_DELIVERABLE_ADDRESS = "allDeliverableAddress";
    public static final String FIREBASE_LOCATION_TEMPORARY_STORAGE = "temporaryStorage";



    public static final String POSTAL_DETAILS_BY_PLACE_NAME_BASE_URL = "http://postalpincode.in/api/postoffice/";
    public static final String POSTAL_DETAILS_BY_PIN_NUMBER_BASE_URL = "http://postalpincode.in/api/pincode/";

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
    public static final String FIREBASE_URL_SAIRA_All_QUERIES = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_QUERIES;
    public static final String FIREBASE_URL_SAIRA_All_FAQS = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_FAQS;
    public static final String FIREBASE_URL_SAIRA_All_DELIVERABLE_ADDRESS = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_DELIVERABLE_ADDRESS;

    public static final String FIREBASE_URL_SAIRA_All_PRESCRIPTIONS_TO_BE_EVALUATED = FIREBASE_URL + "/" + FIREBASE_LOCATION_All_PRESCRIPTIONS_TO_BE_EVALUATED;



    public static final String USER_TYPE_END_USER = "END USER";
    public static final String USER_TYPE_DEVELOPER = "DEVELOPER";
    public static final String USER_TYPE_EMPLOYEE = "EMPLOYEE";
    public static final String USER_TYPE_OWNER = "OWNER";

    public static final String MEDICINE_CATEGORY_PRESCRIPTION = "PRESCRIPTION";
    public static final String MEDICINE_CATEGORY_OTC = "OTC";

    public static final String PAYMENT_METHOD_PREPAID = "PREPAID";
    public static final String PAYMENT_METHOD_COD = "COD";

    //Unused
   /* public static final String MEDICINE_TYPE_CAPSULE = "Capsule";
    public static final String MEDICINE_TYPE_TABLET = "Tablet";
    public static final String MEDICINE_TYPE_SYRUP = "Syrup";
    public static final String MEDICINE_TYPE_INJECTION = "Injection";*/


    public static final String SEARCH_MEDICINE = "searchMedicine";
    public static final String SEARCH_MEDICINE_MANUFACTURER = "searchMedicineManufacturer";
    public static final String SEARCH_MEDICINE_COMPOSITION = "searchMedicineComposition";
    public static final String SEARCH_DISPLAY_CATEGORY = "searchDisplayCategory";
    public static final String SEARCH_POSTER = "searchPoster";
    public static final String SEARCH_ORDER = "searchOrder";
    public static final String SEARCH_ALL_ORDERS = "searchAllOrder";
    public static final String SEARCH_QUERY = "searchQuery";
    public static final String SEARCH_USERS = "searchUsers";
    public static final String SEARCH_ALL_QUERIES_FROM_ALL_USERS = "searchAllQueriesFromAllUsers";
    public static final String SEARCH_AND_CHOOSE_ORDER = "searchAndChooseOrder";
    public static final String SEARCH_AND_CHOOSE_MEDICINE = "searchAndChooseMedicine";
    public static final String SEARCH_FAQ = "searchFaq";
    public static final String SEARCH_PRESCRIPTION_FOR_EVALUATION = "searchPrescriptionForEvaluation";
    public static final String SEARCH_DELIVERABLE_ADDRESS = "searchDeliverableAddress";
    public static final String SEARCH_DEFAULT_MEDICINE_PICS = "searchDefaultMedicinePics";


    public static final String UPLOAD_SUCCESSFUL = "Upload successful";
    public static final String UPDATE_SUCCESSFUL = "Update successful";
    public static final String UPLOAD_FAIL = "Upload failed";
    public static final String UPDATE_FAIL = "Update failed";
    public static final String ITEM_ALREADY_EXISTS = "Item already exists..Upload failed";


    public static final String ACTIVITY_VISIT_PURPOSE_VISIT= "visit";
    public static final String ACTIVITY_VISIT_PURPOSE_SELECT= "select";


    public static final String ORDER_STATUS_PLACED = "ORDER PLACED";
    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_CANCELED = "CANCELED";
    public static final String ORDER_STATUS_UNDER_PROCESS = "UNDER PROCESS";
    public static final String ORDER_STATUS_REQUESTED_FOR_RETURN = "REQUESTED FOR RETURN";
    public static final String ORDER_STATUS_DISPATCHED = "DISPATCHED";
    public static final String ORDER_STATUS_RETURNED = "RETURNED";

    public static final String ACTIVITY_TITLE_PROFILE_PIC = "Profile Pic";


    public static final String QUERY_STATUS_POSTED = "POSTED";
    public static final String QUERY_STATUS_REOPENED = "RE-OPENED";
    public static final String QUERY_STATUS_RUNNING = "RUNNING";
    public static final String QUERY_STATUS_SOLVED = "SOLVED";


}
