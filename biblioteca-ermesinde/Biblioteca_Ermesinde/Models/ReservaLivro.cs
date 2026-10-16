using System.ComponentModel.DataAnnotations;

namespace Biblioteca_Ermesinde.Models
{
    public class ReservaLivro
    {  
        public int Id { get; set; }
        public string TituloLivro { get; set; }
        public string Autor { get; set; }
        public DateTime DataReserva { get; set; }
    }
}