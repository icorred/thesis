package driver_micaz_osgi;


public class EnvEvent extends net.tinyos.message.Message {

    /** The default size of this message type in bytes. */
    public static final int DEFAULT_MESSAGE_SIZE = 19;

    /** The Active Message type associated with this message. */
    public static final int AM_TYPE = 8;

    /** Create a new EnvEvent of size 19. */
    public EnvEvent() {
        super(DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /** Create a new EnvEvent of the given data_length. */
    public EnvEvent(int data_length) {
        super(data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new EnvEvent with the given data_length
     * and base offset.
     */
    public EnvEvent(int data_length, int base_offset) {
        super(data_length, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new EnvEvent using the given byte array
     * as backing store.
     */
    public EnvEvent(byte[] data) {
        super(data);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new EnvEvent using the given byte array
     * as backing store, with the given base offset.
     */
    public EnvEvent(byte[] data, int base_offset) {
        super(data, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new EnvEvent using the given byte array
     * as backing store, with the given base offset and data length.
     */
    public EnvEvent(byte[] data, int base_offset, int data_length) {
        super(data, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new EnvEvent embedded in the given message
     * at the given base offset.
     */
    public EnvEvent(net.tinyos.message.Message msg, int base_offset) {
        super(msg, base_offset, DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new EnvEvent embedded in the given message
     * at the given base offset and length.
     */
    public EnvEvent(net.tinyos.message.Message msg, int base_offset, int data_length) {
        super(msg, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
    /* Return a String representation of this message. Includes the
     * message type name and the non-indexed field values.
     */
    public String toString() {
      String s = "Message <EnvEvent> \n";
      try {
        s += "  [seqNo=0x"+Long.toHexString(get_seqNo())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [id=0x"+Long.toHexString(get_id())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [single_hop_src=0x"+Long.toHexString(get_single_hop_src())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [dst=0x"+Long.toHexString(get_dst())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [type=0x"+Long.toHexString(get_type())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [temperature=0x"+Long.toHexString(get_temperature())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [humidity=0x"+Long.toHexString(get_humidity())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [shortTime=0x"+Long.toHexString(get_shortTime())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [longTime=0x"+Long.toHexString(get_longTime())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      return s;
    }

    // Message-type-specific access methods appear below.

    /////////////////////////////////////////////////////////
    // Accessor methods for field: seqNo
    //   Field type: int, unsigned
    //   Offset (bits): 0
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'seqNo' is signed (false).
     */
    public static boolean isSigned_seqNo() {
        return false;
    }

    /**
     * Return whether the field 'seqNo' is an array (false).
     */
    public static boolean isArray_seqNo() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'seqNo'
     */
    public static int offset_seqNo() {
        return (0 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'seqNo'
     */
    public static int offsetBits_seqNo() {
        return 0;
    }

    /**
     * Return the value (as a int) of the field 'seqNo'
     */
    public int get_seqNo() {
        return (int)getUIntBEElement(offsetBits_seqNo(), 16);
    }

    /**
     * Set the value of the field 'seqNo'
     */
    public void set_seqNo(int value) {
        setUIntBEElement(offsetBits_seqNo(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'seqNo'
     */
    public static int size_seqNo() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'seqNo'
     */
    public static int sizeBits_seqNo() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: id
    //   Field type: int, unsigned
    //   Offset (bits): 16
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'id' is signed (false).
     */
    public static boolean isSigned_id() {
        return false;
    }

    /**
     * Return whether the field 'id' is an array (false).
     */
    public static boolean isArray_id() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'id'
     */
    public static int offset_id() {
        return (16 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'id'
     */
    public static int offsetBits_id() {
        return 16;
    }

    /**
     * Return the value (as a int) of the field 'id'
     */
    public int get_id() {
        return (int)getUIntBEElement(offsetBits_id(), 16);
    }

    /**
     * Set the value of the field 'id'
     */
    public void set_id(int value) {
        setUIntBEElement(offsetBits_id(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'id'
     */
    public static int size_id() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'id'
     */
    public static int sizeBits_id() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: single_hop_src
    //   Field type: int, unsigned
    //   Offset (bits): 32
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'single_hop_src' is signed (false).
     */
    public static boolean isSigned_single_hop_src() {
        return false;
    }

    /**
     * Return whether the field 'single_hop_src' is an array (false).
     */
    public static boolean isArray_single_hop_src() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'single_hop_src'
     */
    public static int offset_single_hop_src() {
        return (32 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'single_hop_src'
     */
    public static int offsetBits_single_hop_src() {
        return 32;
    }

    /**
     * Return the value (as a int) of the field 'single_hop_src'
     */
    public int get_single_hop_src() {
        return (int)getUIntBEElement(offsetBits_single_hop_src(), 16);
    }

    /**
     * Set the value of the field 'single_hop_src'
     */
    public void set_single_hop_src(int value) {
        setUIntBEElement(offsetBits_single_hop_src(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'single_hop_src'
     */
    public static int size_single_hop_src() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'single_hop_src'
     */
    public static int sizeBits_single_hop_src() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: dst
    //   Field type: int, unsigned
    //   Offset (bits): 48
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'dst' is signed (false).
     */
    public static boolean isSigned_dst() {
        return false;
    }

    /**
     * Return whether the field 'dst' is an array (false).
     */
    public static boolean isArray_dst() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'dst'
     */
    public static int offset_dst() {
        return (48 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'dst'
     */
    public static int offsetBits_dst() {
        return 48;
    }

    /**
     * Return the value (as a int) of the field 'dst'
     */
    public int get_dst() {
        return (int)getUIntBEElement(offsetBits_dst(), 16);
    }

    /**
     * Set the value of the field 'dst'
     */
    public void set_dst(int value) {
        setUIntBEElement(offsetBits_dst(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'dst'
     */
    public static int size_dst() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'dst'
     */
    public static int sizeBits_dst() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: type
    //   Field type: short, unsigned
    //   Offset (bits): 64
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'type' is signed (false).
     */
    public static boolean isSigned_type() {
        return false;
    }

    /**
     * Return whether the field 'type' is an array (false).
     */
    public static boolean isArray_type() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'type'
     */
    public static int offset_type() {
        return (64 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'type'
     */
    public static int offsetBits_type() {
        return 64;
    }

    /**
     * Return the value (as a short) of the field 'type'
     */
    public short get_type() {
        return (short)getUIntBEElement(offsetBits_type(), 8);
    }

    /**
     * Set the value of the field 'type'
     */
    public void set_type(short value) {
        setUIntBEElement(offsetBits_type(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'type'
     */
    public static int size_type() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'type'
     */
    public static int sizeBits_type() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: temperature
    //   Field type: int, unsigned
    //   Offset (bits): 72
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'temperature' is signed (false).
     */
    public static boolean isSigned_temperature() {
        return false;
    }

    /**
     * Return whether the field 'temperature' is an array (false).
     */
    public static boolean isArray_temperature() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'temperature'
     */
    public static int offset_temperature() {
        return (72 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'temperature'
     */
    public static int offsetBits_temperature() {
        return 72;
    }

    /**
     * Return the value (as a int) of the field 'temperature'
     */
    public int get_temperature() {
        return (int)getUIntBEElement(offsetBits_temperature(), 16);
    }

    /**
     * Set the value of the field 'temperature'
     */
    public void set_temperature(int value) {
        setUIntBEElement(offsetBits_temperature(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'temperature'
     */
    public static int size_temperature() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'temperature'
     */
    public static int sizeBits_temperature() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: humidity
    //   Field type: int, unsigned
    //   Offset (bits): 88
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'humidity' is signed (false).
     */
    public static boolean isSigned_humidity() {
        return false;
    }

    /**
     * Return whether the field 'humidity' is an array (false).
     */
    public static boolean isArray_humidity() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'humidity'
     */
    public static int offset_humidity() {
        return (88 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'humidity'
     */
    public static int offsetBits_humidity() {
        return 88;
    }

    /**
     * Return the value (as a int) of the field 'humidity'
     */
    public int get_humidity() {
        return (int)getUIntBEElement(offsetBits_humidity(), 16);
    }

    /**
     * Set the value of the field 'humidity'
     */
    public void set_humidity(int value) {
        setUIntBEElement(offsetBits_humidity(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'humidity'
     */
    public static int size_humidity() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'humidity'
     */
    public static int sizeBits_humidity() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: shortTime
    //   Field type: int, unsigned
    //   Offset (bits): 104
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'shortTime' is signed (false).
     */
    public static boolean isSigned_shortTime() {
        return false;
    }

    /**
     * Return whether the field 'shortTime' is an array (false).
     */
    public static boolean isArray_shortTime() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'shortTime'
     */
    public static int offset_shortTime() {
        return (104 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'shortTime'
     */
    public static int offsetBits_shortTime() {
        return 104;
    }

    /**
     * Return the value (as a int) of the field 'shortTime'
     */
    public int get_shortTime() {
        return (int)getUIntBEElement(offsetBits_shortTime(), 16);
    }

    /**
     * Set the value of the field 'shortTime'
     */
    public void set_shortTime(int value) {
        setUIntBEElement(offsetBits_shortTime(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'shortTime'
     */
    public static int size_shortTime() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'shortTime'
     */
    public static int sizeBits_shortTime() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: longTime
    //   Field type: long, unsigned
    //   Offset (bits): 120
    //   Size (bits): 32
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'longTime' is signed (false).
     */
    public static boolean isSigned_longTime() {
        return false;
    }

    /**
     * Return whether the field 'longTime' is an array (false).
     */
    public static boolean isArray_longTime() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'longTime'
     */
    public static int offset_longTime() {
        return (120 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'longTime'
     */
    public static int offsetBits_longTime() {
        return 120;
    }

    /**
     * Return the value (as a long) of the field 'longTime'
     */
    public long get_longTime() {
        return (long)getUIntBEElement(offsetBits_longTime(), 32);
    }

    /**
     * Set the value of the field 'longTime'
     */
    public void set_longTime(long value) {
        setUIntBEElement(offsetBits_longTime(), 32, value);
    }

    /**
     * Return the size, in bytes, of the field 'longTime'
     */
    public static int size_longTime() {
        return (32 / 8);
    }

    /**
     * Return the size, in bits, of the field 'longTime'
     */
    public static int sizeBits_longTime() {
        return 32;
    }

}
