package com.example.android.miwok;

public class Word {
    private String englishTranslation;
    private String miwokTranslation;
    private int image;
    private int pronunciation;

    public Word(String englishTranslation, String miwokTranslation, int pronunciation) {
        this.englishTranslation = englishTranslation;
        this.miwokTranslation = miwokTranslation;
        this.pronunciation = pronunciation;
    }

    public Word(String englishTranslation, String miwokTranslation, int image, int pronunciation) {
        this.englishTranslation = englishTranslation;
        this.miwokTranslation = miwokTranslation;
        this.image = image;
        this.pronunciation = pronunciation;
    }

    public String getEnglishTranslation() {
        return englishTranslation;
    }

    public String getMiwokTranslation() {
        return miwokTranslation;
    }

    public int getImage() {
        return image;
    }

    public int getPronunciation() {
        return pronunciation;
    }
}
