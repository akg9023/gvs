package com.voice.dbRegistration.model;

enum AspiringAshram{
    BRAHMACHARI,
    GRIHASTHA,
    NOT_DECIDED,
    NULL // for married,brahmachari and sanyasi
}

enum Gender{
    MALE,
    FEMALE
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
    PRE_PRIMARY_SCHOOL,
    PRIMARY_SCHOOL,
    MIDDLE_SCHOOL,
    SECONDARY_SCHOOL,
    HIGHER_SECONDARY_SCHOOL,
    DIPLOMA,
    UG,
    PG,
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
