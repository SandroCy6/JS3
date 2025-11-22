package com.tuapp.backend.dto.tipocambio;

public record TipoCambioResponse(
        String buy_price,
        String sell_price,
        String base_currency,
        String quote_currency,
        String date
) {
}
