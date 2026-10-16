using Microsoft.AspNetCore.Mvc;
using Biblioteca_Ermesinde.Models;
namespace Biblioteca_Ermesinde.Controllers{
    [ApiController]
    [Route("Livro")]
    public class LivroController: ControllerBase
    {
        private readonly ILogger<LivroController> _logger;

        private readonly BackEndContext _dbContext;
        private ILogger<LivroController> @object;

        public LivroController(ILogger<LivroController> @object)
        {
            this.@object = @object;
        }

        public LivroController(ILogger<LivroController> logger,BackEndContext dbContext)
        {
            _logger = logger;
            _dbContext=dbContext;
        }

        [HttpGet("Get/{id}")]
        public IActionResult GetLivro(int id)
        {
            var livro_selecionado = _dbContext.Livros.Find(id);
            if (livro_selecionado == null)
            {
                return NotFound($"Livro com ID {id} n�o foi encontrado.");
            }else{
                var resposta = new{
                    Titulo = livro_selecionado.Titulo,
                    Autor = livro_selecionado.Autor,
                    ano = livro_selecionado.Ano
                };
            return Ok(resposta);
            }
        }
    }
}