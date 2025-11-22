import React from "react";
import { useCart } from "../context/CartContext";
import { useCurrency } from "../context/CurrencyContext";
import "./ProductCard.css";

function ProductCard({ producto }) {
  const { addToCart } = useCart();
  const { currency, convertir } = useCurrency();

  const formatPrice = (valor) => {
    const convertido = convertir(valor); // convierte si está en USD
    if (currency === "PEN") {
      return `S/ ${convertido.toFixed(2)}`;
    }
    return `$ ${convertido.toFixed(2)}`;
  };

  const handleAdd = () => {
    addToCart(producto);
  };

  return (
    <div className="product-card">
      <h3>{producto.nombre}</h3>
      <p>{producto.descripcion}</p>

      <p className="product-price">{formatPrice(producto.precio)}</p>
      <p className="product-stock">Stock: {producto.stock} unidades</p>

      <button
        type="button"
        className="btn-add-cart"
        onClick={handleAdd}
      >
        Agregar al carrito
      </button>
    </div>
  );
}

export default ProductCard;
