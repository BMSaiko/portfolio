using Biblioteca_Ermesinde.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Biblioteca_Ermesinde.Controllers
{
    [ApiController]
    [Route("Reserva")]
    public class ReservaController : ControllerBase
    {
        private readonly ILogger<ReservaController> _logger;
        private readonly BackEndContext _dbContext;
        private ILogger<ReservaController> @object;
        private ClientesController clienteController;

        public ReservaController(ILogger<ReservaController> logger, BackEndContext dbContext)
        {
            _logger = logger;
            _dbContext = dbContext;
        }

        public ReservaController(ILogger<ReservaController> @object, ClientesController clienteController)
        {
            this.@object = @object;
            this.clienteController = clienteController;
        }

        [HttpPost]
        [Route("Post")]
        public IActionResult PostReserva([FromBody] ReservaRequest request)
        {

            Cliente cliente1;
            Livro livro1;
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            try
            {
                if (request == null)
                {
                    return BadRequest(new { Mensagem = "Dados de reserva inválidos." });
                }

                List<Cliente> cliente_todos = _dbContext.Clientes.ToList();
                List<Livro> livros_todos = _dbContext.Livros.ToList();

                if (!(cliente_todos.FirstOrDefault(r => r.Id == request.IdCliente) == null))
                {
                    cliente1 = cliente_todos.First();
                }
                else
                {
                    return BadRequest(new { Mensagem = "Dados de reserva inválidos." });
                }

                if (!(livros_todos.FirstOrDefault(r => r.Id == request.IdLivro) == null))
                {
                    livro1 = livros_todos.First();
                }
                else
                {
                    return BadRequest(new { Mensagem = "Dados de reserva inválidos." });
                }

                // Assuming ReservaRequest contains IdCliente and IdLivro
                var newReserva = new Reserva
                {
                    id_Cliente = request.IdCliente,
                    id_Livro = request.IdLivro,
                    data_Reserva = request.DataReserva,
                    cliente = cliente1,
                    livro = livro1
                };


                _dbContext.Reservas.Add(newReserva);
                _dbContext.SaveChanges();

                return StatusCode(201, new { Mensagem = "Reserva realizada com sucesso." });
            }
            catch (DbUpdateException dbUpdateEx)
            {
                // Logar a exceção interna com detalhes adicionais
                Console.WriteLine($"Erro ao salvar alterações no banco de dados. Mensagem: {dbUpdateEx.Message}");

                // Retornar mensagem de erro amigável para o cliente
                return BadRequest(new { Mensagem = "Erro ao fazer reserva. Consulte os logs para obter mais informações." });
            }
        }

        [HttpGet]
        [Route("Get/{id}")]
        public IActionResult GetReservaPorId(int id)
        {
            try
            {
                var reserva = _dbContext.Reservas
                .Include(r => r.cliente)
                .Include(r => r.livro)
                .FirstOrDefault(r => r.Id == id);

                if (reserva != null)
                {
                    var resposta = new
                    {
                        NomeCliente = reserva.cliente.Nome,
                        TituloLivro = reserva.livro.Titulo,
                        DataReserva = reserva.data_Reserva
                    };
                    return Ok(resposta);
                }
                else
                {
                    return NotFound(new { Mensagem = "Reserva não encontrada." });
                }
            }
            catch (Exception ex)
            {
                return BadRequest(new { Mensagem = $"Erro ao obter reserva: {ex.Message}" });
            }
        }

        public OkObjectResult GetReservasPorCliente(int idCliente)
        {
            throw new NotImplementedException();
        }



        [HttpGet]
        [Route("Get/{idCliente}/Livros")]
        public IActionResult ObterReservasPorCliente(int idCliente)
        {
            try
            {
                var reserva = _dbContext.Reservas
                        .Include(r => r.cliente)
                        .Include(r => r.livro)
                        .FirstOrDefault(r => r.id_Cliente == idCliente);
                if (reserva != null)
                {
                    var resposta = new
                    {
                        TituloLivro = reserva.livro.Titulo,
                        Autor = reserva.livro.Autor,
                        Data_reserva = reserva.data_Reserva
                    };
                    return Ok(resposta);
                }
                else
                {
                    return NotFound(new { Mensagem = "Reserva não encontrada." });
                }
            }
            catch (Exception ex)
            {
                return BadRequest(new { Mensagem = $"Erro ao obter reservas do cliente: {ex.Message}" });
            }
        }

        /* private string ObterTituloLivroPorId(int livroId)
        {
            var livroController = new LivroController(null); // Aqui deveria ser utilizado a injeção de dependência, não é uma prática recomendada
            var livro = livroController.GetLivro(livroId) as OkObjectResult;

            if (livro != null)
            {
                var livroData = livro.Value as Livro;
                return livroData?.Titulo;
            }

            return $"Livro {livroId}";
        }
            
        private string ObterAutorLivroPorId(int livroId)
        {
            var livroController = new LivroController(null); // Aqui deveria ser utilizado a injeção de dependência, não é uma prática recomendada
            var livro = livroController.GetLivro(livroId) as OkObjectResult;

            if (livro != null)
            {
                var livroData = livro.Value as Livro;
                return livroData?.Autor;
            }

            return "Autor Desconhecido";
        } */
    }
}

