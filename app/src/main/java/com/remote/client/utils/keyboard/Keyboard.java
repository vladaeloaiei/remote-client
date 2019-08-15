package com.remote.client.utils.keyboard;

import android.util.SparseIntArray;

import com.remote.client.R;

/**
 * Class used to map the android key id with the java key id
 */
public class Keyboard
{
    /* JAVA KEY VALUES */
    private static final int VK_0 = 48;
    private static final int VK_1 = 49;
    private static final int VK_2 = 50;
    private static final int VK_3 = 51;
    private static final int VK_4 = 52;
    private static final int VK_5 = 53;
    private static final int VK_6 = 54;
    private static final int VK_7 = 55;
    private static final int VK_8 = 56;
    private static final int VK_9 = 57;

    private static final int VK_F1  = 112;
    private static final int VK_F2  = 113;
    private static final int VK_F3  = 114;
    private static final int VK_F4  = 115;
    private static final int VK_F5  = 116;
    private static final int VK_F6  = 117;
    private static final int VK_F7  = 118;
    private static final int VK_F8  = 119;
    private static final int VK_F9  = 120;
    private static final int VK_F10 = 121;
    private static final int VK_F11 = 122;
    private static final int VK_F12 = 123;

    private static final int VK_A = 65;
    private static final int VK_B = 66;
    private static final int VK_C = 67;
    private static final int VK_D = 68;
    private static final int VK_E = 69;
    private static final int VK_F = 70;
    private static final int VK_G = 71;
    private static final int VK_H = 72;
    private static final int VK_I = 73;
    private static final int VK_J = 74;
    private static final int VK_K = 75;
    private static final int VK_L = 76;
    private static final int VK_M = 77;
    private static final int VK_N = 78;
    private static final int VK_O = 79;
    private static final int VK_P = 80;
    private static final int VK_Q = 81;
    private static final int VK_R = 82;
    private static final int VK_S = 83;
    private static final int VK_T = 84;
    private static final int VK_U = 85;
    private static final int VK_V = 86;
    private static final int VK_W = 87;
    private static final int VK_X = 88;
    private static final int VK_Y = 89;
    private static final int VK_Z = 90;

    private static final int VK_BACK_SPACE    = 8;
    private static final int VK_TAB           = 9;
    private static final int VK_ENTER         = 10;
    private static final int VK_SHIFT         = 16;
    private static final int VK_CONTROL       = 17;
    private static final int VK_ALT           = 18;
    private static final int VK_CAPS_LOCK     = 20;
    private static final int VK_ESC           = 27;
    private static final int VK_SPACE         = 32;
    private static final int VK_LEFT          = 37;
    private static final int VK_UP            = 38;
    private static final int VK_RIGHT         = 39;
    private static final int VK_DOWN          = 40;
    private static final int VK_COMMA         = 44;
    private static final int VK_MINUS         = 45;
    private static final int VK_PERIOD        = 46;
    private static final int VK_SLASH         = 47;
    private static final int VK_SEMICOLON     = 59;
    private static final int VK_EQUALS        = 61;
    private static final int VK_OPEN_BRACKET  = 91;
    private static final int VK_BACK_SLASH    = 92;
    private static final int VK_CLOSE_BRACKET = 93;
    private static final int VK_DELETE        = 127;
    private static final int VK_PRINT_SCREEN  = 154;
    private static final int VK_BACK_QUOTE    = 192;
    private static final int VK_QUOTE         = 222;
    private static final int VK_WINDOWS       = 524;
    private static final int VK_CONTEXT_MENU  = 525;

    /* Map the button id with the java key value */
    private static final SparseIntArray JAVA_KEY_MAP = new SparseIntArray()
    {
        {
            append(R.id.key0, VK_0);
            append(R.id.key1, VK_1);
            append(R.id.key2, VK_2);
            append(R.id.key3, VK_3);
            append(R.id.key4, VK_4);
            append(R.id.key5, VK_5);
            append(R.id.key6, VK_6);
            append(R.id.key7, VK_7);
            append(R.id.key8, VK_8);
            append(R.id.key9, VK_9);

            append(R.id.keyF1, VK_F1);
            append(R.id.keyF2, VK_F2);
            append(R.id.keyF3, VK_F3);
            append(R.id.keyF4, VK_F4);
            append(R.id.keyF5, VK_F5);
            append(R.id.keyF6, VK_F6);
            append(R.id.keyF7, VK_F7);
            append(R.id.keyF8, VK_F8);
            append(R.id.keyF9, VK_F9);
            append(R.id.keyF10, VK_F10);
            append(R.id.keyF11, VK_F11);
            append(R.id.keyF12, VK_F12);

            append(R.id.keyA, VK_A);
            append(R.id.keyB, VK_B);
            append(R.id.keyC, VK_C);
            append(R.id.keyD, VK_D);
            append(R.id.keyE, VK_E);
            append(R.id.keyF, VK_F);
            append(R.id.keyG, VK_G);
            append(R.id.keyH, VK_H);
            append(R.id.keyI, VK_I);
            append(R.id.keyJ, VK_J);
            append(R.id.keyK, VK_K);
            append(R.id.keyL, VK_L);
            append(R.id.keyM, VK_M);
            append(R.id.keyN, VK_N);
            append(R.id.keyO, VK_O);
            append(R.id.keyP, VK_P);
            append(R.id.keyQ, VK_Q);
            append(R.id.keyR, VK_R);
            append(R.id.keyS, VK_S);
            append(R.id.keyT, VK_T);
            append(R.id.keyU, VK_U);
            append(R.id.keyV, VK_V);
            append(R.id.keyW, VK_W);
            append(R.id.keyX, VK_X);
            append(R.id.keyY, VK_Y);
            append(R.id.keyZ, VK_Z);

            append(R.id.keyBackspace, VK_BACK_SPACE);
            append(R.id.keyTab, VK_TAB);
            append(R.id.keyEnter, VK_ENTER);
            append(R.id.keyLShift, VK_SHIFT);
            append(R.id.keyRShift, VK_SHIFT);
            append(R.id.keyLCtrl, VK_CONTROL);
            append(R.id.keyRCtrl, VK_CONTROL);
            append(R.id.keyAlt, VK_ALT);
            append(R.id.keyCapsLock, VK_CAPS_LOCK);
            append(R.id.keyEsc, VK_ESC);
            append(R.id.keySpace, VK_SPACE);
            append(R.id.keyLeft, VK_LEFT);
            append(R.id.keyUp, VK_UP);
            append(R.id.keyRight, VK_RIGHT);
            append(R.id.keyDown, VK_DOWN);
            append(R.id.keyComma, VK_COMMA);
            append(R.id.keyMinus, VK_MINUS);
            append(R.id.keyDot, VK_PERIOD);
            append(R.id.keySlash, VK_SLASH);
            append(R.id.keySemicolon, VK_SEMICOLON);
            append(R.id.keyEquals, VK_EQUALS);
            append(R.id.keyOpenBracket, VK_OPEN_BRACKET);
            append(R.id.keyBackSlash, VK_BACK_SLASH);
            append(R.id.keyCloseBracket, VK_CLOSE_BRACKET);
            append(R.id.keyDelete, VK_DELETE);
            append(R.id.keyPrint, VK_PRINT_SCREEN);
            append(R.id.keyBackQuote, VK_BACK_QUOTE);
            append(R.id.keyQuote, VK_QUOTE);
            append(R.id.keyWindows, VK_WINDOWS);
            append(R.id.keyContextMenu, VK_CONTEXT_MENU);
        }
    };

    /**
     * Get the java mapped key
     *
     * @param keyId The key id
     * @return The java mapped key
     */
    public static int getJavaKeyValue(int keyId)
    {
        return JAVA_KEY_MAP.get(keyId);
    }
}
