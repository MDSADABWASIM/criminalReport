package mdsadabwasimcom.criminal.database;

/*
here we define our database schema , what's the name of our table and
what are its columns.
 */
public class CrimeDbSchema {
    public static final class CrimeTable{
        public static final String NAME="crime";
        public static final class Cols{
            public static final String UUID ="uuid";
            public static final String TITLE="title";
            public static final String DATE="date";
            public static final String TIME="time";
            public static final String SOLVED="solved";
            public static final String SUSPECT="suspect";
            public static final String CALL="call";
            public static final String CALL_POLICE ="call_police";
            public static final String CONTACT_ID= "contact_id";
        }
    }
}
