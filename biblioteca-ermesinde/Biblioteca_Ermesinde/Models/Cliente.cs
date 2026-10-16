namespace Biblioteca_Ermesinde.Models{

public class Cliente
{
    public int Id { get; set; }
    public string Nome {  get; set; }
    public string Endereco { get; set; }
    public string Contato { get; set; }

    public List<Reserva> Reservas { get; set; } = new List<Reserva>();
    }
}