package EmpleadoInterfaz;

/**
 * Clase que representa un cliente y permite el acceso a sus atributos mediante métodos getter y setter.
 * Sigue el patrón JavaBean para encapsulación de datos.
 */
public class ClienteSetGet {
    // Atributos de la clase
    private int id_cliente;         // Identificador único del cliente
    private String cedula;          // Número de cédula del cliente
    private String nombre;          // Nombre completo del cliente
    private String direccion;       // Dirección de residencia del cliente
    private String telefono;        // Número de teléfono del cliente
    private String email;           // Correo electrónico del cliente

    /**
     * Constructor completo para crear una instancia de ClienteSetGet.
     *
     * @param id_cliente Identificador único del cliente
     * @param nombre Nombre completo del cliente
     * @param cedula Número de cédula del cliente
     * @param telefono Número de teléfono del cliente
     * @param direccion Dirección de residencia del cliente
     * @param email Correo electrónico del cliente
     */
    public ClienteSetGet(int id_cliente, String nombre, String cedula,
                         String telefono, String direccion, String email) {
        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
    }

    // Métodos getter y setter para cada atributo

    /**
     * Obtiene el ID del cliente.
     * @return El identificador único del cliente
     */
    public int getId_cliente() {
        return id_cliente;
    }

    /**
     * Establece el ID del cliente.
     * @param id_cliente El nuevo identificador único del cliente
     */
    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    /**
     * Obtiene la cédula del cliente.
     * @return El número de cédula del cliente
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Establece la cédula del cliente.
     * @param cedula El nuevo número de cédula del cliente
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * Obtiene el nombre del cliente.
     * @return El nombre completo del cliente
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del cliente.
     * @param nombre El nuevo nombre completo del cliente
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la dirección del cliente.
     * @return La dirección de residencia del cliente
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección del cliente.
     * @param direccion La nueva dirección de residencia del cliente
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Obtiene el teléfono del cliente.
     * @return El número de teléfono del cliente
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono del cliente.
     * @param telefono El nuevo número de teléfono del cliente
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene el email del cliente.
     * @return El correo electrónico del cliente
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email del cliente.
     * @param email El nuevo correo electrónico del cliente
     */
    public void setEmail(String email) {
        this.email = email;
    }
}