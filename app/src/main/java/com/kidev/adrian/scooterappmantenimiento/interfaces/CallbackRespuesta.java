package com.kidev.adrian.scooterappmantenimiento.interfaces;

import com.kidev.adrian.scooterappmantenimiento.util.Util;
import java.util.Map;

/**
 *
 * @author agarcia.gonzalez
 */
public interface CallbackRespuesta {
    
    void success (Map<String,String> contenido);                             // El código está entre 200 y 299
    void error (Map<String,String> contenido, Util.CODIGO codigoError);      // El código es 3 o está entre 400 y 999
    
}
