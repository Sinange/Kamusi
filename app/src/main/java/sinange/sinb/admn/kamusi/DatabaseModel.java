package sinange.sinb.admn.kamusi;

public class DatabaseModel {
    String kundilaManeno;
    String maanaPili;
    String mfanoPili;
    String Ngeli;
    public String getMaanaPili() {
        return maanaPili;
    }
    public void setMaanaPili(String maanaPili) {
        this.maanaPili = maanaPili;
    }

    public String getNgeli() {
        return Ngeli;
    }
    public void setNgeli(String ngeli) {
        Ngeli = ngeli;
    }

    public String getMfanoPili() {
        return mfanoPili;
    }
    public void setMfanoPili(String mfanoPili) {
        this.mfanoPili = mfanoPili;
    }

    public void setKundilaManeno(String kundilaManeno) {
        this.kundilaManeno = kundilaManeno;
    }

    public String getKundilaManeno() {
        return kundilaManeno;
    }


    public DatabaseModel(String kundilaManeno, String jina,String ngeli, String maana,String maanaPili, String kisawe, String mnyambuliko,String mfano,String mfanoPili) {
        this.kundilaManeno = kundilaManeno;
        this.maanaPili = maanaPili;
        this.mfanoPili = mfanoPili;
        this.jina = jina;
        this.Ngeli=ngeli;
        this.maana = maana;
        this.kisawe = kisawe;
        this.mnyambuliko = mnyambuliko;
        this.mfano = mfano;
    }

    String jina;
    String maana;
    String kisawe;
    String mnyambuliko;
    String  mfano;

    public String getJina() {
        return jina;
    }

    public void setJina(String jina) {
        this.jina = jina;
    }

    public String getMaana() {
        return maana;
    }

    public void setMaana(String maana) {
        this.maana = maana;
    }

    public String getKisawe() {
        return kisawe;
    }

    public void setKisawe(String kisawe) {
        this.kisawe = kisawe;
    }

    public String getMnyambuliko() {
        return mnyambuliko;
    }

    public void setMnyambuliko(String mnyambuliko) {
        this.mnyambuliko = mnyambuliko;
    }

    public String getMfano() {
        return mfano;
    }

    public void setMfano(String mfano) {
        this.mfano = mfano;
    }

    public DatabaseModel(String jina, String maana,String maanaPili, String kisawe, String mnyambuliko, String mfano,String mfanoPili) {
        this.jina = jina;
        this.maana = maana;
        this.maanaPili=maanaPili;
        this.mfanoPili=mfanoPili;
        this.kisawe = kisawe;
        this.mnyambuliko = mnyambuliko;
        this.mfano = mfano;
    }

}
