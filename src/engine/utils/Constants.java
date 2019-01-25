/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utils;

/**
 *
 * @author filip
 */
public class Constants {
    
    public static final String APPLICATION_DIR = System.getProperty("user.dir");
    public static final String SPRITES_DIR = APPLICATION_DIR + "/res/images/sprites/";
    public static final String CONFIG_DIR = APPLICATION_DIR + "/res/config/";

    public static final int CIRCLE_SEGMENTS = 16;
    public static final float LINE_WIDTH = 2.0f;
    public static final int DEFAULT_WINDOW_WIDTH = 800;
    public static final int DEFAULT_WINDOW_HEIGHT = 600;
    public static final double G = 9.80665d;
    public static final int FIELD_WIDTH = 896;
    public static final int FIELD_HEIGHT = 1152;
    public static final int OFFSCREEN_LIMIT = 64;
    public static final int GRID_RESOLUTION = 32;

    public static final double DELTA = 1E6;
    public static final double DELTA_IN_SECONDS = DELTA * 1E-9;
}
