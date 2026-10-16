namespace Biblioteca_Ermesinde.Models
{
    public class ReservaResponse
    {
        public int IdReserva { get; set; }
        public string NomeCliente { get; set; }
        public string TituloLivro { get; set; }
        public DateTime DataReserva { get; set; }
    }
}
