namespace Biblioteca_Ermesinde.Models
{
    public class Reserva
    {
        public int Id { get; set; }
        public int id_Cliente { get; set; }
        public int id_Livro{ get; set; }
        public DateTime data_Reserva { get; set; }

        public Cliente cliente { get; set; }
        public Livro livro { get; set; }
    }
}
