namespace Biblioteca_Ermesinde.Models{

    public class Livro
    {
        public int Id { get; set; }
        public string Titulo {  get; set; }
        public string Autor { get; set; }
        public int Ano { get; set; }
        public List<Reserva> Reservas { get; set; } = new List<Reserva>();
    }
}