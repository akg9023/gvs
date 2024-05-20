package com.voice.v1.dbRegistration.model;

enum AspiringAshram{
    BRAHMACHARI,
    GRIHASTHA,
    NOT_DECIDED,
    NULL // for married,brahmachari and sanyasi
}



enum MaritialStatus{
    UNMARRIED,
    MARRIED,
    DIVORCED,
    BRAHMACHARI,
    SANYASI
}



enum Language{
    BENGALI,
    ENGLISH,
    HINDI,
    GUJARATI,
    KANNADA,
    MARATHI,
    MAITHILI,
    ORIYA,
    NEPALI,
    PUNJABI
}

enum Education{
    NO_EDUCATION,
    UPTO_5th_STD,
    UPTO_10th_STD,
    UPTO_12th_STD,
    DIPLOMA,
    GRADUATION,
    POST_GRADUATION,
    DOCTORATE,
    POST_DOCTORATE
}

enum Occupation{
    EMPLOYEED_FULL_TIME,
    EMPLOYEED_PART_TIME,
    SELF_EMPLOYED,
    UNEMPLOYED,
    HOMEMAKER,
    RETIRED,
    STUDENT
}

enum Prividege{
    SUPER_ADMIN,
    ADMIN,
    GUARDIAN,
    VOLUNTEER,
    USER
}