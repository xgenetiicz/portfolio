export default function TerminalIntro() {
  return (
    <div
      className="w-full min-h-[60vh] flex items-center justify-center bg-[#05070a] font-mono"
      style={{ backgroundImage: "radial-gradient(circle at 50% 30%, #0c1210 0%, #05070a 70%)" }}
    >
      <div className="w-[min(100vw,600px)] bg-[#0b0f0d] border border-[#1c2620] rounded-xl overflow-hidden shadow-2xl">
        <div className="h-[34px] bg-[#101512] flex items-center gap-2 px-3.5 border-b border-[#1c2620]">
          <span className="w-2.5 h-2.5 rounded-full bg-[#ff5f57]"></span>
          <span className="w-2.5 h-2.5 rounded-full bg-[#febc2e]"></span>
          <span className="w-2.5 h-2.5 rounded-full bg-[#28c840]"></span>
          <span className="ml-2 text-[11px] text-[#8a9c92]">genti@buildhub — zsh</span>
        </div>
        <div className="p-5 text-[#d7e6dd] text-[15px] leading-loose" style={{ ["--accent" as string]: "#39ff9c" }}>
          <div className="whitespace-nowrap overflow-hidden">
            <span className="text-[var(--accent)]">genti@buildhub</span>
            <span className="text-[#7d8f85]">:~$ </span>
            <span className="typed inline-block overflow-hidden whitespace-nowrap w-0 align-bottom border-r-2 border-accent">
              whoami
            </span>
          </div>
          <div
            className="opacity-0 translate-y-1.5 text-[var(--accent)] font-bold text-[15px] mt-3.5 animate-fadeUpA"
            style={{ animationDelay: "1.3s" }}
          >
            Heyooo! my name is Genti Rudi and welcome to my portfolio. :D
          </div>
          <div
            className="opacity-0 translate-y-1.5 text-[#d7e6dd] animate-fadeUpB"
            style={{ animationDelay: "2.5s" }}
          >
            Java Developer with strong focus on Spring Framework.
          </div>
          <div
            className="opacity-0 translate-y-1.5 text-[#d7e6dd] animate-fadeUpC"
            style={{ animationDelay: "2.5s" }}
          >
            Enjoy your ride through my portfolio page!
            <span
              className="inline-block w-2 h-[15px] bg-[var(--accent)] ml-1 align-middle opacity-0 animate-tailFade"
              style={{ animationDelay: "2.2s" }}
            ></span>
          </div>
        </div>
      </div>
    </div>
  );
}