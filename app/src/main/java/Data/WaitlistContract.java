package Data;

import android.provider.BaseColumns;

public class WaitlistContract
{

    //Write on it the table name and all columns name
    public static final class WaitlistEntry implements BaseColumns
    {

        public static final String TABLE_NAME="waitlist";
        public static final String COLUMN_GUEST_NAME="gestName";
        public static final String COLUMN_PARTY_SIZE="partySize";
        public static final String COLUMN_TIMESTAMP="timestamp";
    }
}
