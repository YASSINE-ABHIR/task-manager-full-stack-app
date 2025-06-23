
// import React from "react";
// import { Link } from "react-router-dom";
// import {
//     Github,
//     Linkedin,
//     Facebook
// } from "react-bootstrap-icons";

// const Footer = () => (
//     <footer className="footer footer--sticky">
//         {/* — Brand & Copyright — */}
//         <div className="footer__section">
//             <h3 className="footer__title">Task Manager</h3>
//             <p>&copy; {new Date().getFullYear()} Task Manager.<br /> Hahn Software. All rights reserved.</p>

//         </div>

//         {/* — Navigation interne — */}
//         <div className="footer__section">
//             <h4 className="footer__subtitle">Navigation</h4>
//             <Link to="/dashboard">Dashboard</Link>
//             <Link to="/about">About</Link>
//             {/* Lien externe Hahn Software */}
//             <a
//                 href="https://www.hahn-software.io/"
//                 target="_blank"
//                 rel="noopener noreferrer"
//             >
//                 Hahn Software
//             </a>
//         </div>

//         {/* — Légal — */}
//         <div className="footer__section">
//             <h4 className="footer__subtitle">Legal</h4>
//             <Link to="/privacy">Privacy Policy</Link>
//             <Link to="/terms">Terms & Conditions</Link>
//         </div>

//         {/* — Réseaux sociaux — */}
//         {/* Social */}


//             <div className="footer__social">
//                 <a
//                     href="https://github.com/your-org"
//                     aria-label="GitHub"
//                     target="_blank"
//                     rel="noopener noreferrer"
//                 >
//                     <Github size={24} />
//                 </a>
//                 <a
//                     href="https://www.linkedin.com/company/hahn-softwareentwicklung"
//                     aria-label="LinkedIn"
//                     target="_blank"
//                     rel="noopener noreferrer"
//                 >
//                     <Linkedin size={24} />
//                 </a>
//                 {/* Icônes brand “maison” */}
//                 <a
//                     href="https://www.kununu.com/de/hahn-softwareentwicklung1"
//                     aria-label="Kununu"
//                     target="_blank"
//                     rel="noopener noreferrer"
//                 >
//                     <KununuIcon width="24" height="24" />
//                 </a>
//                 <a
//                     href="https://www.xing.com/pages/hahn-software"
//                     aria-label="Xing"
//                     target="_blank"
//                     rel="noopener noreferrer"
//                 >
//                     <XingIcon width="24" height="24" />
//                 </a>
//                 <a
//                     href="https://www.tiktok.com/@hahnsoftwaretrainees"
//                     aria-label="TikTok"
//                     target="_blank"
//                     rel="noopener noreferrer"
//                 >
//                     <TikTokIcon width="24" height="24" />
//                 </a>
//                 <a
//                     href="https://www.instagram.com/hahnsoftware"
//                     aria-label="Instagram"
//                     target="_blank"
//                     rel="noopener noreferrer"
//                 >
//                     <InstagramIcon width="24" height="24" />
//                 </a>
//                 <a
//                     href="https://www.youtube.com/channel/UCE_mXI2wY2FkGPEhXAsml5Q"
//                     aria-label="YouTube"
//                     target="_blank"
//                     rel="noopener noreferrer"
//                 >
//                     <YoutubeIcon width="24" height="24" />
//                 </a>
//                 {/* Facebook si besoin */}
//                 <a
//                     href="#"
//                     aria-label="Facebook"
//                     target="_blank"
//                     rel="noopener noreferrer"
//                 >
//                     <Facebook size={24} />
//                 </a>
//             </div>
//     </footer>
// );

// export default Footer;
// src/components/layout/Footer.jsx
import React from "react";
import { Link } from "react-router-dom";
import { Github, Linkedin, Facebook,Instagram } from "react-bootstrap-icons";

// --- SVG inline pour les réseaux que react-bootstrap-icons ne couvre pas ---
const KununuIcon = (props) => (
    <svg viewBox="0 0 27 27" {...props}>
        <path
            d="M22.277 7.009 21.716 7.562c-.08.082-.21.082-.291 0l-.073-.073c-.081-.082-.09-.212-.008-.285l.593-.593a.141.141 0 0 0 0-.198l-.097-.097a.208.208 0 0 0-.27 0l-.602.584c-.081.082-.211.074-.292-.008l-.073-.073a.203.203 0 0 1 0-.285l.602-.594c.309-.3.813-.307 1.121-.007l.098.098c.309.309.309.796 0 1.097Zm-.008 2.576-.098.098c-.309.308-.812.301-1.121-.008l-.561-.553c-.081-.082-.09-.212-.008-.294l.074-.073c.081-.082.219-.091.3-.009l.601.601a.208.208 0 0 0 .27 0l.098-.098a.141.141 0 0 0 0-.198l-.602-.585c-.081-.081-.073-.202.009-.284l.073-.073c.082-.081.212-.081.293 0l.602.585c.308.301.308.796-.008 1.097Zm-2.138-3.885-.073.073c-.081.082-.219.091-.3.009l-.601-.594a.208.208 0 0 0-.27 0l-.098.098a.141.141 0 0 0 0 .198l.602.585c.081.081.09.211.009.284l-.074.073c-.082.081-.211.09-.292.008l-.602-.585c-.309-.301-.309-.796 0-1.097l.097-.098c.309-.3.813-.307 1.121-.007l.561.553c.081.081.09.211.008.293Zm-.008 2.414-.601.585c-.309.309-.813.309-1.121.008l-.098-.098c-.309-.301-.309-.796 0-1.097l.561-.553c.081-.081.219-.09.3-.008l.073.073c.081.082.09.212.008.294l-.602.585a.141.141 0 0 0 0 .198l.097.097c.081.082.211.082.292 0l.602-.585c.309-.301.813-.294 1.121.015l.073.073c.081.082.081.203 0 .284Zm-2.635 9.388-.041 2.506c0 .214-.187.402-.415.402h-2.465c-.228 0-.415-.188-.415-.402v-2.506h-.014v-.817c0-.871-.724-1.581-1.621-1.581h-.871c-.885 0-1.622.71-1.622 1.581v.817h-.027v2.506c0 .214-.187.402-.415.402H7.116c-.228 0-.415-.188-.415-.402V6.054c0-.214.187-.402.415-.402h2.479c.215 0 .402.188.416.402v7.061c.509-.173 1.058-.281 1.635-.281h.83v-.013c.884 0 1.622-.71 1.622-1.582l.054-.817c.026-.187.2-.335.401-.335h2.479c.201 0 .375.148.402.335v.817c0 1.233-.468 2.346-1.246 3.203.791.871 1.273 1.997 1.273 3.243Zm-3.988-18.502C6.044 0 0 6.044 0 13.5S6.044 27 13.5 27 27 20.956 27 13.5 20.956 0 13.5 0Z"
            fill="currentColor"
        />
    </svg>
);

// Xing, TikTok, Instagram, YouTube : icônes simplifiées (paths raccourcis)
const XingIcon = (props) => (
    <svg viewBox="0 0 20 20" {...props}>
        <path
            d="M13.963 5.66c-.063.13-.092.192-.092.192L11.35 11.037l1.512 3.111s.03.061.094.191c.063.129.073.328-.179.328h-1.426a.42.42 0 0 1-.348-.198l-1.512-3.111 2.521-5.185s.087-.178.157-.321c.07-.143.234-.197.348-.197h1.426c.252 0 .24.197.178.328ZM7.514 11.482s-.088.178-.157.32c-.073.143-.242.198-.356.198H5.553c-.256 0-.244-.198-.182-.328.065-.129.095-.191.095-.191l1.153-2.334-.64-1.296s-.03-.062-.094-.192c-.063-.129-.074-.327.182-.327h1.448c.114 0 .283.055.356.197.068.143.156.321.156.321l.641 1.296-1.152 2.333ZM10 0C4.477 0 0 4.477 0 10s4.477 10 10 10 10-4.477 10-10S15.523 0 10 0Z"
            fill="currentColor"
        />
    </svg>
);

const TikTokIcon = (props) => (
    <svg viewBox="0 0 20 20" {...props}>
        <path
            d="M10 0a10 10 0 1 0 0 20 10 10 0 0 0 0-20Zm5.717 8.725a4.15 4.15 0 0 1-3.314-1.596v5.493c0 2.243-1.818 4.061-4.06 4.061-2.243 0-4.061-1.818-4.061-4.061 0-2.242 1.818-4.06 4.06-4.06.086 0 .17.007.254.013v2.001a2.003 2.003 0 0 0-.254-.017 2.05 2.05 0 1 0 2.049 2.05V3.318h1.913a3.494 3.494 0 0 0 3.664 3.183v2.224Z"
            fill="currentColor"
        />
    </svg>
);

const InstagramIcon = (props) => (
    <svg viewBox="0 0 20 20" {...props}>
        <path
            d="M15.837 12.245c0 1.698-1.428 3.081-3.184 3.081H7.347c-1.755 0-3.184-1.383-3.184-3.081V7.11c0-1.699 1.429-3.081 3.184-3.081h5.306c1.756 0 3.184 1.382 3.184 3.081v5.135ZM10 0C4.477 0 0 4.333 0 9.678c0 5.345 4.477 9.677 10 9.677 5.523 0 10-4.332 10-9.677C20 4.333 15.523 0 10 0Z"
            fill="currentColor"
        />
    </svg>
);

const YoutubeIcon = (props) => (
    <svg viewBox="0 0 20 20" fill="none" {...props}>
        <path
            d="M10 20a10 10 0 1 0 0-20 10 10 0 0 0 0 20Zm-7-12.092C3 6.302 4.302 5 5.908 5h8.184C15.698 5 17 6.302 17 7.908v4.09c0 1.606-1.302 2.908-2.908 2.908H5.908C4.302 14.906 3 13.604 3 11.999V7.908Zm5.298 4.07 3.829-1.826a.186.186 0 0 0 0-.356L8.301 7.936a.186.186 0 0 0-.223.174v3.766c0 .144.149.24.298.174Z"
            fill="currentColor"
        />
    </svg>
);

const Footer = () => (
    <footer className="footer footer--sticky">
        {/* Brand */}
        <div className="footer__section">
            <h3 className="footer__title">Task Manager</h3>
            <p>
                &copy; {new Date().getFullYear()} Task Manager.<br />
                Hahn Software. All rights reserved.
            </p>
        </div>

        {/* Navigation */}
        <div className="footer__section">
            <h4 className="footer__subtitle">Navigation</h4>
            <Link to="/dashboard">Dashboard</Link>
            <Link to="/about">About</Link>
            <a
                href="https://www.hahn-software.io/"
                target="_blank"
                rel="noopener noreferrer"
            >
                Hahn Software
            </a>
        </div>

        {/* Legal */}
        <div className="footer__section">
            <h4 className="footer__subtitle">Legal</h4>
            <Link to="/privacy">Privacy Policy</Link>
            <Link to="/terms">Terms & Conditions</Link>
        </div>

        {/* Social */}
        <div className="footer__section">
            <h4 className="footer__subtitle">Stay connected!</h4>
            <p className="footer__text">
                Follow us on your preferred social media!
            </p>

            <div className="footer__social">
                <a
                    href="https://github.com/your-org"
                    aria-label="GitHub"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    <Github size={24} />
                </a>
                <a
                    href="https://www.linkedin.com/company/hahn-softwareentwicklung"
                    aria-label="LinkedIn"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    <Linkedin size={24} />
                </a>
                {/* Icônes brand “maison” */}
                <a
                    href="https://www.kununu.com/de/hahn-softwareentwicklung1"
                    aria-label="Kununu"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    <KununuIcon width="24" height="24" />
                </a>
                <a
                    href="https://www.xing.com/pages/hahn-software"
                    aria-label="Xing"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    <XingIcon width="24" height="24" />
                </a>
                <a
                    href="https://www.tiktok.com/@hahnsoftwaretrainees"
                    aria-label="TikTok"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    <TikTokIcon width="24" height="24" />
                </a>
                <a
                    href="https://www.instagram.com/hahnsoftware"
                    aria-label="Instagram"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    <Instagram width="24" height="24" />
                </a>
                <a
                    href="https://www.youtube.com/channel/UCE_mXI2wY2FkGPEhXAsml5Q"
                    aria-label="YouTube"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    <YoutubeIcon width="24" height="24" />
                </a>
                {/* Facebook si besoin */}
                <a
                    href="#"
                    aria-label="Facebook"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    <Facebook size={24} />
                </a>
            </div>
        </div>
    </footer>
);

export default Footer;
