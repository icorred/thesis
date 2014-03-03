// Variables to control overall beahvior of the dashboard
var AUTO_REFRESH = true;
var AUTO_SCROLL = true; // scroll through the tabs
// These control how often we refresh the model and view (in milliseconds)
var MODEL_REFRESH_PERIOD = 30000;
var VIEW_REFRESH_PERIOD = 5000;
// Delay inserted between the first call to update model and the first
// rendering of the view
var INITIAL_VIEW_REFRESH_LAG = 1000;
var DELAY_THRESHOLD = MODEL_REFRESH_PERIOD * 3; // warn if we haven't heard in this long

// Number of light readings to store
var LIGHT_READING_COUNT = 10;

var TEMP_READING_COUNT = 10;

var HUM_READING_COUNT = 10;

// These constants control the view ...
var NUMBER_OF_PAGES = 5; // number of tabs
var ROWS_PER_PAGE = 3;
var ROWS_PER_PAGE_SUN = 2;
var COLS_PER_PAGE = 5;
var DEVICES_PER_PAGE = 4;
var TOTAL_DEVICES_VIEWABLE = NUMBER_OF_PAGES * DEVICES_PER_PAGE;

// These control the appearance of the graphic representing each SPOT
var TILE_WIDTH = 220;
var TILE_HEIGHT = 160;
var TEXT_TOP_OFFSET = 7;
var PEN_IMAGE_HEIGHT = 12;
var PEN_IMAGE_WIDTH = 12;
var PLOT_LEFT_OFFSET = 0;
var PLOT_BOTTOM_OFFSET = 30;
var PLOT_WIDTH = 220;
var PLOT_HEIGHT = 80;
var LED_BAR_WIDTH = 118;
var LED_BAR_HEIGHT = 23;
var LED_BAR_LEFT_OFFSET = 1;
var LED_BAR_BOTTOM_OFFSET = 1;


var TEMP_BAR_WIDTH = 10;
var TEMP_BAR_OFFSET = 11;
var TEMP_BAR_GAP = 12;
var TEMP_LABEL_GAP = 1;

var HUM_BAR_WIDTH = 10;
var HUM_BAR_OFFSET = 11;
var HUM_BAR_GAP = 12;
var HUM_LABEL_GAP = 1;

var LIGHT_BAR_WIDTH = 10;
var LIGHT_BAR_OFFSET = 11;
var LIGHT_BAR_GAP = 12;
var LIGHT_LABEL_GAP = 1;

var BATTERY_BAR_WIDTH = 22;
var BATTERY_BAR_HEIGHT = 11;
var BATTERY_BAR_LEFT_OFFSET = 1;
var BATTERY_BAR_TOP_OFFSET = 32;
var CHARGING_INDICATOR_SIZE = BATTERY_BAR_HEIGHT/2;

var READING1_MIN = 0;  // light readings
var READING1_MAX = 40.0;
var READING1_FACTOR = (READING1_MAX - READING1_MIN)/PLOT_HEIGHT;

var READING2_MIN = 0;  // light readings
var READING2_MAX = 100.0;
var READING2_FACTOR = (READING2_MAX - READING2_MIN)/PLOT_HEIGHT;

var READING3_MIN = 2800;  // light readings
var READING3_MAX = 10000;
var READING3_FACTOR = (READING3_MAX - READING3_MIN)/PLOT_HEIGHT;