export const API_URL = "http://localhost:8080";

export async function fetchProductos() {
  const res = await fetch(`${API_URL}/productos`);

  if (!res.ok) {
    throw new Error("Error al cargar productos");
  }

  const data = await res.json(); 
  return data;                  
}
