package driver_micaz_osgi;


public class Greenhouse extends net.tinyos.message.Message {

    /** The default size of this message type in bytes. */
    public static final int DEFAULT_MESSAGE_SIZE = 18;

    /** The Active Message type associated with this message. */
    public static final int AM_TYPE = 7;

    /** Create a new Greenhouse of size 18. */
    public Greenhouse() {
        super(DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /** Create a new Greenhouse of the given data_length. */
    public Greenhouse(int data_length) {
        super(data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new Greenhouse with the given data_length
     * and base offset.
     */
    public Greenhouse(int data_length, int base_offset) {
        super(data_length, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new Greenhouse using the given byte array
     * as backing store.
     */
    public Greenhouse(byte[] data) {
        super(data);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new Greenhouse using the given byte array
     * as backing store, with the given base offset.
     */
    public Greenhouse(byte[] data, int base_offset) {
        super(data, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new Greenhouse using the given byte array
     * as backing store, with the given base offset and data length.
     */
    public Greenhouse(byte[] data, int base_offset, int data_length) {
        super(data, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new Greenhouse embedded in the given message
     * at the given base offset.
     */
    public Greenhouse(net.tinyos.message.Message msg, int base_offset) {
        super(msg, base_offset, DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new Greenhouse embedded in the given message
     * at the given base offset and length.
     */
    public Greenhouse(net.tinyos.message.Message msg, int base_offset, int data_length) {
        super(msg, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
    /* Return a String representation of this message. Includes the
     * message type name and the non-indexed field values.
     */
    public String toString() {
      String s = "Message <Greenhouse> \n";
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
        s += "  [type=0x"+Long.toHexString(get_type())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [error=0x"+Long.toHexString(get_error())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [Voltage_data=0x"+Long.toHexString(get_Voltage_data())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [Temp_data=0x"+Long.toHexString(get_Temp_data())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [Hum_data=0x"+Long.toHexString(get_Hum_data())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [VisLight_data=0x"+Long.toHexString(get_VisLight_data())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [InfLight_data=0x"+Long.toHexString(get_InfLight_data())+"]\n";
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
    // Accessor methods for field: type
    //   Field type: short, unsigned
    //   Offset (bits): 48
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
        return (48 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'type'
     */
    public static int offsetBits_type() {
        return 48;
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
    // Accessor methods for field: error
    //   Field type: short, unsigned
    //   Offset (bits): 56
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'error' is signed (false).
     */
    public static boolean isSigned_error() {
        return false;
    }

    /**
     * Return whether the field 'error' is an array (false).
     */
    public static boolean isArray_error() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'error'
     */
    public static int offset_error() {
        return (56 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'error'
     */
    public static int offsetBits_error() {
        return 56;
    }

    /**
     * Return the value (as a short) of the field 'error'
     */
    public short get_error() {
        return (short)getUIntBEElement(offsetBits_error(), 8);
    }

    /**
     * Set the value of the field 'error'
     */
    public void set_error(short value) {
        setUIntBEElement(offsetBits_error(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'error'
     */
    public static int size_error() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'error'
     */
    public static int sizeBits_error() {
        return 8;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: Voltage_data
    //   Field type: int, unsigned
    //   Offset (bits): 64
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'Voltage_data' is signed (false).
     */
    public static boolean isSigned_Voltage_data() {
        return false;
    }

    /**
     * Return whether the field 'Voltage_data' is an array (false).
     */
    public static boolean isArray_Voltage_data() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'Voltage_data'
     */
    public static int offset_Voltage_data() {
        return (64 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'Voltage_data'
     */
    public static int offsetBits_Voltage_data() {
        return 64;
    }

    /**
     * Return the value (as a int) of the field 'Voltage_data'
     */
    public int get_Voltage_data() {
        return (int)getUIntBEElement(offsetBits_Voltage_data(), 16);
    }

    /**
     * Set the value of the field 'Voltage_data'
     */
    public void set_Voltage_data(int value) {
        setUIntBEElement(offsetBits_Voltage_data(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'Voltage_data'
     */
    public static int size_Voltage_data() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'Voltage_data'
     */
    public static int sizeBits_Voltage_data() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: Temp_data
    //   Field type: int, unsigned
    //   Offset (bits): 80
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'Temp_data' is signed (false).
     */
    public static boolean isSigned_Temp_data() {
        return false;
    }

    /**
     * Return whether the field 'Temp_data' is an array (false).
     */
    public static boolean isArray_Temp_data() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'Temp_data'
     */
    public static int offset_Temp_data() {
        return (80 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'Temp_data'
     */
    public static int offsetBits_Temp_data() {
        return 80;
    }

    /**
     * Return the value (as a int) of the field 'Temp_data'
     */
    public int get_Temp_data() {
        return (int)getUIntBEElement(offsetBits_Temp_data(), 16);
    }

    /**
     * Set the value of the field 'Temp_data'
     */
    public void set_Temp_data(int value) {
        setUIntBEElement(offsetBits_Temp_data(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'Temp_data'
     */
    public static int size_Temp_data() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'Temp_data'
     */
    public static int sizeBits_Temp_data() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: Hum_data
    //   Field type: int, unsigned
    //   Offset (bits): 96
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'Hum_data' is signed (false).
     */
    public static boolean isSigned_Hum_data() {
        return false;
    }

    /**
     * Return whether the field 'Hum_data' is an array (false).
     */
    public static boolean isArray_Hum_data() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'Hum_data'
     */
    public static int offset_Hum_data() {
        return (96 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'Hum_data'
     */
    public static int offsetBits_Hum_data() {
        return 96;
    }

    /**
     * Return the value (as a int) of the field 'Hum_data'
     */
    public int get_Hum_data() {
        return (int)getUIntBEElement(offsetBits_Hum_data(), 16);
    }

    /**
     * Set the value of the field 'Hum_data'
     */
    public void set_Hum_data(int value) {
        setUIntBEElement(offsetBits_Hum_data(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'Hum_data'
     */
    public static int size_Hum_data() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'Hum_data'
     */
    public static int sizeBits_Hum_data() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: VisLight_data
    //   Field type: int, unsigned
    //   Offset (bits): 112
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'VisLight_data' is signed (false).
     */
    public static boolean isSigned_VisLight_data() {
        return false;
    }

    /**
     * Return whether the field 'VisLight_data' is an array (false).
     */
    public static boolean isArray_VisLight_data() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'VisLight_data'
     */
    public static int offset_VisLight_data() {
        return (112 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'VisLight_data'
     */
    public static int offsetBits_VisLight_data() {
        return 112;
    }

    /**
     * Return the value (as a int) of the field 'VisLight_data'
     */
    public int get_VisLight_data() {
        return (int)getUIntBEElement(offsetBits_VisLight_data(), 16);
    }

    /**
     * Set the value of the field 'VisLight_data'
     */
    public void set_VisLight_data(int value) {
        setUIntBEElement(offsetBits_VisLight_data(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'VisLight_data'
     */
    public static int size_VisLight_data() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'VisLight_data'
     */
    public static int sizeBits_VisLight_data() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: InfLight_data
    //   Field type: int, unsigned
    //   Offset (bits): 128
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'InfLight_data' is signed (false).
     */
    public static boolean isSigned_InfLight_data() {
        return false;
    }

    /**
     * Return whether the field 'InfLight_data' is an array (false).
     */
    public static boolean isArray_InfLight_data() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'InfLight_data'
     */
    public static int offset_InfLight_data() {
        return (128 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'InfLight_data'
     */
    public static int offsetBits_InfLight_data() {
        return 128;
    }

    /**
     * Return the value (as a int) of the field 'InfLight_data'
     */
    public int get_InfLight_data() {
        return (int)getUIntBEElement(offsetBits_InfLight_data(), 16);
    }

    /**
     * Set the value of the field 'InfLight_data'
     */
    public void set_InfLight_data(int value) {
        setUIntBEElement(offsetBits_InfLight_data(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'InfLight_data'
     */
    public static int size_InfLight_data() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'InfLight_data'
     */
    public static int sizeBits_InfLight_data() {
        return 16;
    }

}
