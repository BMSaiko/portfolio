namespace Biblioteca_Ermesinde.Models
{
    public class ReservaRequest
    {
        public int IdCliente { get; set; }
        public int IdLivro { get; set; }
        public DateTime DataReserva { get; set; }
    }
}