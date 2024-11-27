package com.example.mychat.utils;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class GetTextUtils {
    public static String getTextFromInput(TextInputLayout textInputLayout) {
        assert textInputLayout != null;

        return (Objects.requireNonNull(textInputLayout.getEditText()).getText().toString());
    }
}
