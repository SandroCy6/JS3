import { useEffect, useState } from "react";
import { fetchProductos } from "../api";
import ProductCard from "../componentes/ProductCard";
import "./Home.css";

function Home() {
  const [productos, setProductos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const cargarProductos = async () => {
      try {
        const data = await fetchProductos();
        setProductos(data);
      } catch (err) {
        console.error(err);
        setError("No se pudo obtener los productos.");
      } finally {
        setCargando(false);
      }
    };

    cargarProductos();
  }, []);

  return (
    <section className="home">
      <h1>Catálogo de productos</h1>

      {cargando && <p>Cargando productos...</p>}

      {!cargando && error && <p className="error">{error}</p>}

      {!cargando && !error && productos.length === 0 && (
        <p>No hay productos disponibles.</p>
      )}

      {!cargando && !error && productos.length > 0 && (
        <div className="productos-grid">
          {productos.map((p) => (
            <ProductCard key={p.id} producto={p} />
          ))}
        </div>
      )}
    </section>
  );
}

export default Home;
