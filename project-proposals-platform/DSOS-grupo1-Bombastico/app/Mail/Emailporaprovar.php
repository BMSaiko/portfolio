<?php

namespace App\Mail;

use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Mail\Mailable;
use Illuminate\Mail\Mailables\Content;
use Illuminate\Mail\Mailables\Envelope;
use Illuminate\Queue\SerializesModels;

class Emailporaprovar extends Mailable
{
    use Queueable, SerializesModels;

    public $proposta;
    public $emailDestinatario;


    /**
     * Create a new message instance.
     *
     * @return void
     */
    public function __construct(\App\Models\propostas $proposta, $emailDestinatario)
    {
        $this->proposta = $proposta;
        $this->emailDestinatario = $emailDestinatario;
    }

    public function build()
    {
        return $this->to($this->proposta->emailDestinatario)
        ->subject('Assunto do E-mail')
        ->view('emails.emailporaprovar');
    }
    

    /**
     * Get the message envelope.
     */
    public function envelope(): Envelope
    {
        return new Envelope(
            subject: 'Email',
        );
    }

    /**
     * Get the message content definition.
     */
    public function content(): Content
    {
        return new Content(
            view: 'emails.emailporaprovar',
        );
    }

    /**
     * Get the attachments for the message.
     *
     * @return array<int, \Illuminate\Mail\Mailables\Attachment>
     */
    public function attachments(): array
    {
        return [];
    }
}
