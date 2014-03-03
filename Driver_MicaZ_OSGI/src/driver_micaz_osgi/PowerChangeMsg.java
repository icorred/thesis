package driver_micaz_osgi;
/**
 * This class is automatically generated by mig. DO NOT EDIT THIS FILE.
 * This class implements a Java interface to the 'PowerChangeMsg'
 * message type.
 */

public class PowerChangeMsg extends net.tinyos.message.Message {

    /** The default size of this message type in bytes. */
    public static final int DEFAULT_MESSAGE_SIZE = 4;

    /** The Active Message type associated with this message. */
    public static final int AM_TYPE = 6;

    /** Create a new PowerChangeMsg of size 4. */
    public PowerChangeMsg() {
        super(DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /** Create a new PowerChangeMsg of the given data_length. */
    public PowerChangeMsg(int data_length) {
        super(data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new PowerChangeMsg with the given data_length
     * and base offset.
     */
    public PowerChangeMsg(int data_length, int base_offset) {
        super(data_length, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new PowerChangeMsg using the given byte array
     * as backing store.
     */
    public PowerChangeMsg(byte[] data) {
        super(data);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new PowerChangeMsg using the given byte array
     * as backing store, with the given base offset.
     */
    public PowerChangeMsg(byte[] data, int base_offset) {
        super(data, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new PowerChangeMsg using the given byte array
     * as backing store, with the given base offset and data length.
     */
    public PowerChangeMsg(byte[] data, int base_offset, int data_length) {
        super(data, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new PowerChangeMsg embedded in the given message
     * at the given base offset.
     */
    public PowerChangeMsg(net.tinyos.message.Message msg, int base_offset) {
        super(msg, base_offset, DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new PowerChangeMsg embedded in the given message
     * at the given base offset and length.
     */
    public PowerChangeMsg(net.tinyos.message.Message msg, int base_offset, int data_length) {
        super(msg, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
    /* Return a String representation of this message. Includes the
     * message type name and the non-indexed field values.
     */
    public String toString() {
      String s = "Message <PowerChangeMsg> \n";
      try {
        s += "  [source=0x"+Long.toHexString(get_source())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [power_value=0x"+Long.toHexString(get_power_value())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      return s;
    }

    // Message-type-specific access methods appear below.

    /////////////////////////////////////////////////////////
    // Accessor methods for field: source
    //   Field type: int, unsigned
    //   Offset (bits): 0
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'source' is signed (false).
     */
    public static boolean isSigned_source() {
        return false;
    }

    /**
     * Return whether the field 'source' is an array (false).
     */
    public static boolean isArray_source() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'source'
     */
    public static int offset_source() {
        return (0 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'source'
     */
    public static int offsetBits_source() {
        return 0;
    }

    /**
     * Return the value (as a int) of the field 'source'
     */
    public int get_source() {
        return (int)getUIntBEElement(offsetBits_source(), 16);
    }

    /**
     * Set the value of the field 'source'
     */
    public void set_source(int value) {
        setUIntBEElement(offsetBits_source(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'source'
     */
    public static int size_source() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'source'
     */
    public static int sizeBits_source() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: power_value
    //   Field type: int, unsigned
    //   Offset (bits): 16
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'power_value' is signed (false).
     */
    public static boolean isSigned_power_value() {
        return false;
    }

    /**
     * Return whether the field 'power_value' is an array (false).
     */
    public static boolean isArray_power_value() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'power_value'
     */
    public static int offset_power_value() {
        return (16 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'power_value'
     */
    public static int offsetBits_power_value() {
        return 16;
    }

    /**
     * Return the value (as a int) of the field 'power_value'
     */
    public int get_power_value() {
        return (int)getUIntBEElement(offsetBits_power_value(), 16);
    }

    /**
     * Set the value of the field 'power_value'
     */
    public void set_power_value(int value) {
        setUIntBEElement(offsetBits_power_value(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'power_value'
     */
    public static int size_power_value() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'power_value'
     */
    public static int sizeBits_power_value() {
        return 16;
    }

}
