// Variables to control overall beahvior of the dashboard
var AUTO_REFRESH = true;
var AUTO_SCROLL = true; // scroll through the tabs
// These control how often we refresh the model and view (in milliseconds)
var MODEL_REFRESH_PERIOD = 10000;
var VIEW_REFRESH_PERIOD = 10000;
// Delay inserted between the first call to update model and the first
// rendering of the view
var INITIAL_VIEW_REFRESH_LAG = 1000;
var DELAY_THRESHOLD = MODEL_REFRESH_PERIOD * 3; // warn if we haven't heard in this long

var GAP_IN_READINGS = 1;

// Number of heart rate readings to store
var HEART_READING_COUNT = 12;

// Number of body temperature readings to store
var TEMP_READING_COUNT = 12;


// These constants control the view ...
var NUMBER_OF_PAGES = 3; // number of tabs
var ROWS_PER_PAGE = 1;
var COLS_PER_PAGE = 6;
var DEVICES_PER_PAGE = ROWS_PER_PAGE * COLS_PER_PAGE;
var TOTAL_DEVICES_VIEWABLE = NUMBER_OF_PAGES * DEVICES_PER_PAGE;

// These control the appearance of the graphic representing each SPOT
var TILE_WIDTH = 200;
var TILE_HEIGHT = 200;
var TEXT_TOP_OFFSET = 0;
var PEN_IMAGE_HEIGHT = 12;
var PEN_IMAGE_WIDTH = 12;

var PLOT_LEFT_OFFSET = 100;
var PLOT_BOTTOM_OFFSET = 0;
var PLOT_WIDTH = 100;
var PLOT_HEIGHT = 140;

var LED_BAR_WIDTH = 100;
var LED_BAR_HEIGHT = 160;
var LED_BAR_LEFT_OFFSET = 20;
var LED_BAR_BOTTOM_OFFSET = 1;

var LIGHT_BAR_WIDTH = 10;
var LIGHT_BAR_OFFSET = 11;
var LIGHT_BAR_GAP = 12;
var LIGHT_LABEL_GAP = 1;

var HEART_BAR_WIDTH = 8;
var HEART_BAR_HEIGHT = 240;
var HEART_BAR_OFFSET = 20;
var HEART_BAR_GAP = 10;
var HEART_LABEL_GAP = 1;

var BATTERY_BAR_WIDTH = 30;
var BATTERY_BAR_HEIGHT = 11;
var BATTERY_BAR_LEFT_OFFSET = 1;
var BATTERY_BAR_TOP_OFFSET = 32;
var CHARGING_INDICATOR_SIZE = BATTERY_BAR_HEIGHT/2;

var READING1_MIN = 0;  // light readings
var READING1_MAX = 180.0;
var READING1_FACTOR = (READING1_MAX - READING1_MIN)/PLOT_HEIGHT;

var READING2_MIN = 0;  // heart readings
var READING2_MAX = 240.0;
var READING2_FACTOR = (READING2_MAX - READING2_MIN)/PLOT_HEIGHT;

var READING3_MIN = 0;  // body temp readings
var READING3_MAX = 245.0;
var READING3_FACTOR = (READING3_MAX - READING3_MIN)/PLOT_HEIGHT;
