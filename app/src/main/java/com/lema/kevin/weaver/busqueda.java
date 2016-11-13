package com.lema.kevin.weaver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 2/11/16.
 */

public class Busqueda {
    private ArrayList<String> matches;
    private Map <String,String> answers;

    public Busqueda(ArrayList<String> matches){
        this.matches=matches;
        answers = new HashMap<String, String>();
        answers.put("hola", "vete a cagar");
        answers.put("buenas tardes", "vete a cagar");
        answers.put("qué tiempo hace", "Mira por la ventana vago");
        answers.put("cómo estás", "como el culo");
        answers.put("qué opinas de diego", "es una excelentísima persona");
        answers.put("qué haces mañana", "nada");
    }


    public String buscar(){

        for(int i=0;i<matches.size();i++){
            for ( String key : answers.keySet() ) {
                if (matches.get(i).equalsIgnoreCase(key)){
                    return (answers.get(key));
                }
            }
        }
        return (matches.get(0));
    }

}
