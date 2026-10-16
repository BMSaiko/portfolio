using Microsoft.AspNetCore.Mvc;
using Biblioteca_Ermesinde.Models;
namespace Biblioteca_Ermesinde.Controllers{
    [ApiController]
    [Route("Cliente")]
    public class ClientesController : ControllerBase
    {
        private readonly ILogger<ClientesController> _logger;

        private readonly BackEndContext _dbContext;
        private ILogger<ClientesController> @object;

        public ClientesController(ILogger<ClientesController> @object)
        {
            this.@object = @object;
        }

        public ClientesController(ILogger<ClientesController> logger,BackEndContext dbContext)
        {
            _logger = logger;
            _dbContext=dbContext;
        }

        [HttpGet("Get/{id}")]
        public IActionResult GetCliente(int id)
        {
            var cliente_selecionado = _dbContext.Clientes.Find(id);
            if(cliente_selecionado==null){
                return NotFound($"Cliente com ID {id} não foi encontrado.");             
            }else{
                var resposta = new{
                    Nome = cliente_selecionado.Nome,
                    Endereco = cliente_selecionado.Endereco,
                    contato = cliente_selecionado.Contato
                };
                return Ok(resposta);
            }
            
        }
    }
}


